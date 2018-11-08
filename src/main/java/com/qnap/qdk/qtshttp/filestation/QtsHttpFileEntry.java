package com.qnap.qdk.qtshttp.filestation;

/**
 * File or folder information class
 * @author Edison
 *
 */
public class QtsHttpFileEntry {
	private boolean mDir;            /** Is file or folder */
	private long mSize;              /** File or folder size */
	private String mPath;            /** File or folder path */
	private String mName;            /** File or folder name */
	
	public QtsHttpFileEntry() {
		
	}
	
	/**
	 * Constructor with parameters.
	 * @param dir Is file or folder.
	 * @param size File or folder size. 
	 * @param path File or folder path.
	 * @param name File or folder name.
	 */
	public QtsHttpFileEntry(boolean dir, long size, String path, String name) {
		this.mDir = dir;
		this.mSize = size;
		this.mPath = path;
		this.mName = name;
	}
	
	/**
	 * Is folder or file.
	 * @return true Folder entry.
	 * @return false File entry.
	 */
	public boolean isDir() {
		return this.mDir;
	}
	
	/**
	 * Get file/folder size.
	 * @return File/folder size.
	 */
	public long getFileSize() {
		return this.mSize;
	}
	
	/**
	 * Get file/folder path.
	 * @return File/folder path.
	 */
	public String getFilePath() {
		return this.mPath;
	}
	
	/**
	 * Get file/folder name.	
	 * @return File/folder name.
	 */
	public String getFileName() {
		return this.mName;
	}
	
	/**
	 * Set QtsHttpFileEntry is file or folder.
	 * @param dir Is file or folder.
	 */
	public void setDir(boolean dir) {
		this.mDir = dir;
	}
	
	/**
	 * Set file or folder size.
	 * @param size File or folder size.
	 */
	public void setFileSize(long size) {
		this.mSize = size;
	}
	
	/**
	 * Set file or folder path.
	 * @param path File or folder path.
	 */
	public void setFilePath(String path) {
		this.mPath = path;
	}
	
	/**
	 * Set file or folder name.
	 * @param name File or folder name.
	 */
	public void setFileName(String name) {
		this.mName = name;
	}
}
