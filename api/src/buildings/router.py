from fastapi import APIRouter, Depends, Header, HTTPException, status, File, Form, UploadFile
from typing import List, Optional
from sqlalchemy.orm import Session
from database import get_db 
from auth.utils import validate_admin_role, get_current_user_id
from utils.FileUploadService import FileUploadService
from buildings.dto. CreateBuildingDto import CreateBuildingDto
from models import Building, User

router = APIRouter(prefix="/buildings", tags=["Buildings"])
fileUploader = FileUploadService()

@router.post("/")
async def create_building(
    name: str = Form(...),
    university: str = Form(...),
    description: Optional[str] = Form(None),
    address: str = Form(...),
    latitude: str = Form(...),
    longitude: str = Form(...),
    images: List[UploadFile] = File([]),
    db: Session = Depends(get_db)):

    image_urls = await fileUploader.save_files(name, name, images)
    new_building_data = {
            "name": name,
            "university": university,
            "address": address,
            "description": description,
            "imageUrls": image_urls,
            "latitude": float(latitude),
            "longitude": float(longitude),
        }
    new_building = Building(**new_building_data)
    db.add(new_building)
    db.commit()
    db.refresh(new_building)
    return new_building


@router.get("/")
def find_all(user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    buildings = db.query(Building).all()
    return buildings


@router.get("/{id}")
def find_one(id: int, user_id: int = Depends(get_current_user_id),  db: Session = Depends(get_db)):
    return db.query(Building).filter(Building.id == id).first()


@router.delete("/{id}/force")
async def force_remove(id: int, user_id: int = Depends(validate_admin_role),  db: Session = Depends(get_db)):
    building = db.query(Building).filter(Building.id == id).first()
    await fileUploader.delete_files(building.name)
    db.delete(building)
    db.commit()
    return {"message": "Building deleted"}