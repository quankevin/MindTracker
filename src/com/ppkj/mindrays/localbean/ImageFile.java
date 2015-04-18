package com.ppkj.mindrays.localbean;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageFile implements Parcelable {

	private String photoName;
	private String photoPath;
	private long photoDate;
	private String photoDateString;
	private int photoTotal;
	private int isSelect;
	private String paths;

	public void setPaths(String paths) {
		this.paths = paths;
	}

	public String getPaths() {
		return paths;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public long getPhotoDate() {
		return photoDate;
	}

	public void setPhotoDate(long photoDate) {
		this.photoDate = photoDate;
	}

	public String getPhotoDateString() {
		return photoDateString;
	}

	public void setPhotoDateString(String photoDateString) {
		this.photoDateString = photoDateString;
	}

	public int getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(int isSelect) {
		this.isSelect = isSelect;
	}

	public int getPhotoTotal() {
		return photoTotal;
	}

	public void setPhotoTotal(int photoTotal) {
		this.photoTotal = photoTotal;
	}

	public ImageFile() {
	}

	public ImageFile(Parcel in) {
		photoTotal = in.readInt();
		isSelect = in.readInt();
		photoDate = in.readLong();
		photoName = in.readString();
		photoPath = in.readString();
		paths = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeInt(photoTotal);
		out.writeInt(isSelect);
		out.writeLong(photoDate);
		out.writeString(photoName);
		out.writeString(photoPath);
		out.writeString(paths);
	}

	public static final Parcelable.Creator<ImageFile> CREATOR = new Creator<ImageFile>() {

		@Override
		public ImageFile[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ImageFile[size];
		}

		@Override
		public ImageFile createFromParcel(Parcel in) {
			// TODO Auto-generated method stub
			return new ImageFile(in);
		}
	};
}
