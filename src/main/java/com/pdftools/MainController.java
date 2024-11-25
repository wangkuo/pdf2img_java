package com.pdftools;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    @FXML
    private ListView<File> fileListView;
    @FXML
    private ImageView previewImageView;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // 添加ListView选择监听器
        fileListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    previewPDF(newValue);
                }
            }
        );
    }

    @FXML
    private void handleSelectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF文件", "*.pdf")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        
        if (files != null) {
            fileListView.getItems().clear(); // 清除原有选择
            fileListView.getItems().addAll(files);
            if (!files.isEmpty()) {
                fileListView.getSelectionModel().select(0); // 选择第一个文件
                previewPDF(files.get(0));
            }
        }
    }

    private void previewPDF(File file) {
        try {
            // 首先验证文件
            if (!isValidPDF(file)) {
                showError("无法预览：文件可能已损坏或不是有效的PDF文件");
                return;
            }

            Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
            
            PDDocument document = PDDocument.load(file);
            if (document.getNumberOfPages() == 0) {
                showError("无法预览：PDF文件页数为0");
                document.close();
                return;
            }

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 96);
            Image image = convertToFxImage(bufferedImage);
            previewImageView.setImage(image);
            document.close();
        } catch (Exception e) {
            showError("预览PDF时出错: " + e.getMessage());
            previewImageView.setImage(null);
        }
    }

    private boolean isValidPDF(File file) {
        if (!file.exists() || !file.canRead()) {
            return false;
        }

        try (PDDocument document = PDDocument.load(file)) {
            // 尝试加载文档，如果成功则说明是有效的PDF
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void handleConvert() {
        List<File> files = fileListView.getItems();
        if (files.isEmpty()) {
            showError("请先选择PDF文件");
            return;
        }

        new Thread(() -> {
            int total = files.size();
            int current = 0;
            int failed = 0;

            for (File file : files) {
                try {
                    if (!isValidPDF(file)) {
                        updateStatus("跳过无效文件: " + file.getName());
                        failed++;
                        continue;
                    }

                    updateStatus("正在转换: " + file.getName());
                    convertPDFToImage(file);
                    current++;
                    updateProgress((double) current / total);
                } catch (Exception e) {
                    failed++;
                    showError("转换文件时出错: " + e.getMessage());
                }
            }
            
            String completionMessage = String.format("转换完成！成功：%d，失败：%d", 
                                                   current, failed);
            updateStatus(completionMessage);
        }).start();
    }

    private void convertPDFToImage(File pdfFile) throws Exception {
        Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (document.getNumberOfPages() == 0) {
                throw new Exception("PDF文件页数为0");
            }

            PDFRenderer renderer = new PDFRenderer(document);
            String fileName = pdfFile.getName();
            // 使用正则表达式替换.pdf或.PDF扩展名
            String baseFileName = fileName.replaceAll("(?i)\\.pdf$", "");
            
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300);
                String outputPath;
                if (document.getNumberOfPages() == 1) {
                    outputPath = pdfFile.getParent() + File.separator + 
                                baseFileName + ".png";
                } else {
                    outputPath = pdfFile.getParent() + File.separator + 
                                baseFileName + "_" + (page + 1) + ".png";
                }
                ImageIO.write(image, "PNG", new File(outputPath));
            }
        }
    }

    private Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return wr;
    }

    private void showError(String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void updateStatus(String status) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(status));
    }

    private void updateProgress(double progress) {
        javafx.application.Platform.runLater(() -> progressBar.setProgress(progress));
    }

    @FXML
    private void handleClear() {
        fileListView.getItems().clear();
        previewImageView.setImage(null);
        progressBar.setProgress(0);
        statusLabel.setText("准备就绪");
    }
} 