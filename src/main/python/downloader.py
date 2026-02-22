import sys
import os
from pytubefix import YouTube
from pytubefix.exceptions import PytubeFixError as PytubeError

def download_video(url, format_type, output_path):
    try:
        yt = YouTube(url)
        
        if format_type == 'mp4':
            # Filter for progressive streams (both audio and video) and pick the highest resolution
            # Note: Progressive streams usually max out at 720p. 
            # 1080p+ requires ffmpeg merging which is not available here.
            stream = yt.streams.filter(progressive=True, file_extension='mp4').order_by('resolution').desc().first()
        elif format_type == 'mp3':
            # Get the audio stream with the highest bitrate (ABR)
            stream = yt.streams.filter(only_audio=True).order_by('abr').desc().first()
        else:
            print(f"Error: Unsupported format {format_type}")
            sys.exit(1)

        if not stream:
            print(f"Error: No stream found for format {format_type}")
            sys.exit(1)

        print(f"Downloading: {yt.title} in {format_type}...")
        out_file = stream.download(output_path=output_path)
        
        # If mp3, rename the file extension
        if format_type == 'mp3':
            base, ext = os.path.splitext(out_file)
            new_file = base + '.mp3'
            if os.path.exists(new_file):
                os.remove(new_file)
            os.rename(out_file, new_file)
            print(f"Success: {new_file}")
        else:
            print(f"Success: {out_file}")

    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) < 4:
        print("Usage: python downloader.py <url> <format> <output_path>")
        sys.exit(1)
    
    url = sys.argv[1]
    format_type = sys.argv[2]
    output_path = sys.argv[3]
    
    download_video(url, format_type, output_path)
