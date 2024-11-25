# PDF转图片工具

这是一个简单易用的PDF转图片转换工具，支持批量转换PDF文件为图片格式。

## 功能特点

- 支持单个或批量PDF文件转换
- 实时预览PDF文件内容
- 支持将PDF每页转换为单独的图片文件
- 保持原文件名，仅更改扩展名为.png
- 简洁直观的用户界面
- 显示转换进度

## 使用说明

1. 点击"选择PDF文件"按钮选择一个或多个PDF文件
2. 在预览窗口中查看PDF内容
3. 点击"开始转换"按钮进行转换
4. 转换完成后，图片文件将保存在原PDF文件所在目录

## 运行要求

- Java 11 或更高版本

## 安装和运行

### 方法1：直接运行jar文件
1. 确保已安装Java 11或更高版本
2. 双击运行 pdf-to-image-converter-1.0-SNAPSHOT.jar
   或在命令行中运行：
   ```
   java -jar pdf-to-image-converter-1.0-SNAPSHOT.jar
   ```

### 方法2：使用启动脚本
Windows:
- 双击运行 run.bat

Linux/Mac:
1. 给脚本添加执行权限：
   ```
   chmod +x run.sh
   ```
2. 运行脚本：
   ```
   ./run.sh
   ```

## 从源码构建

1. 克隆项目
2. 运行Maven打包命令：
   ```
   mvn clean package
   ```
3. 打包后的文件在target目录下

## 技术架构

- Java 11
- JavaFX 用于构建用户界面
- Apache PDFBox 用于PDF处理
- Maven 用于项目管理

## 依赖项

- org.apache.pdfbox:pdfbox:2.0.24
- org.openjfx:javafx-controls:11
- org.openjfx:javafx-fxml:11 