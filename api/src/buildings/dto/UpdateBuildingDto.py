from typing import Optional, List
from pydantic import BaseModel, Field
from buildings.dto import CreateBuildingDto

class UpdateBuildingDto(BaseModel):
    name: Optional[str] = Field(None, min_length=1)
    university: Optional[str] = Field(None, min_length=1)
    description: Optional[str] = None
    imageUrls: Optional[List[str]] = None
    address: Optional[str] = Field(None, min_length=1)
    latitude: Optional[str] = Field(None, min_length=1)
    longitude: Optional[str] = Field(None, min_length=1)