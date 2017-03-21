package org.blazer.dataservice.entity;

public class DSUpload {

	private Integer id;
	private Integer userId;
	private String fileName;
	private String fileFullName;
	private String fileSuffix;
	private String filePath;
	private String fileOldName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileOldName() {
		return fileOldName;
	}

	public void setFileOldName(String fileOldName) {
		this.fileOldName = fileOldName;
	}

	public String getFileFullName() {
		return fileFullName;
	}

	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}

	@Override
	public String toString() {
		return "DSUpload [id=" + id + ", userId=" + userId + ", fileName=" + fileName + ", fileFullName=" + fileFullName + ", fileSuffix=" + fileSuffix + ", filePath="
				+ filePath + ", fileOldName=" + fileOldName + "]";
	}

}
