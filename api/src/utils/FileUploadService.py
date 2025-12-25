import os
import shutil
from uuid import uuid4
from typing import List
from fastapi import UploadFile, HTTPException
from PIL import Image

ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"}
MAX_FILE_SIZE_MB = 5
BASE_UPLOAD_PATH = "images"

class FileUploadService:
    def __init__(self, upload_dir: str = BASE_UPLOAD_PATH):
        self.upload_dir = upload_dir
        os.makedirs(self.upload_dir, exist_ok=True)

    async def save_files(self, folder_name: str, file_name: str ,files: List[UploadFile]) -> List[str]:
        folder_path = os.path.join(self.upload_dir, folder_name)
        os.makedirs(folder_path, exist_ok=True)

        image_urls = []

        for index, file in enumerate(files):
            extension = file.filename.split(".")[-1].lower()

            if extension not in ALLOWED_EXTENSIONS:
                raise HTTPException(status_code=400, detail=f"File type {extension} not allowed")

            contents = await file.read()

            if len(contents) > MAX_FILE_SIZE_MB * 1024 * 1024:
                raise HTTPException(status_code=400, detail="File too large")

            # Optionally validate it’s a real image
            try:
                Image.open(file.file).verify()
            except Exception:
                raise HTTPException(status_code=400, detail="Invalid image file")

            filename = f"{file_name}-{uuid4().hex}.{extension}"
            file_path = os.path.join(folder_path, filename)

            with open(file_path, "wb") as f:
                f.write(contents)
            image_urls.append(file_path)

        return image_urls


    async def delete_files(self, folder_name: str) -> None:
        folder_path = os.path.join(self.upload_dir, folder_name)
        if os.path.exists(folder_path) and os.path.isdir(folder_path):
            shutil.rmtree(folder_path)
        else:
            raise HTTPException(status_code=404, detail=f"Folder '{folder_name}' not found")