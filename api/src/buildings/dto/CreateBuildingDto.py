from pydantic import BaseModel, Field
from typing import Optional, List

class CreateBuildingDto(BaseModel):
    name: str = Field(..., min_length=1)
    university: str = Field(..., min_length=1)
    description: Optional[str] = None
    imageUrls: List[str] = Field(default_factory=list)
    address: str = Field(..., min_length=1)
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)
