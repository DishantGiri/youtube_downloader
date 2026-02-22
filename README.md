# YouTube Downloader Service

A Spring Boot application that uses Python's `pytube` library to download YouTube videos (Shorts) and Audio (MP3).

## Prerequisites

- **Java 21** or higher
- **Python 3.12** or higher
- **pip** (Python package manager)

## Setup

1. **Install Python dependencies:**
   ```bash
   pip install pytube
   ```

2. **Run the Spring Boot application:**
   On Windows:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   On Linux/macOS:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or open the project in your favorite IDE (IntelliJ, Eclipse, VSCode) and run `YoutubeDownloaderApplication.java`.

## How to Use

1. Open your browser and go to `http://localhost:8070`
2. Paste a YouTube URL (Video or Short).
3. Select the format (MP4 for Video, MP3 for Audio).
4. Click **DOWNLOAD NOW**.

## Project Structure

- `src/main/java`: Spring Boot backend code.
- `src/main/python`: Python script (`downloader.py`) that performs the download.
- `src/main/resources/static`: Frontend interface.
- `downloads/`: Temporary folder where videos are stored before being streamed to you.

## Note on Pytube
YouTube frequently updates its API, which may cause `pytube` to break. If you encounter issues, try updating pytube:
```bash
pip install --upgrade pytube
```
