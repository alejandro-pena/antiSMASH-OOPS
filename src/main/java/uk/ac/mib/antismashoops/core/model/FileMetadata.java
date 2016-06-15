package uk.ac.mib.antismashoops.core.model;

public class FileMetadata
{
	private String fileName;
	private String fileSize;
	private String fileType;
	private byte[] bytes;

	public FileMetadata()
	{

	}

	public FileMetadata(String fileName, String fileSize, String fileType, byte[] bytes)
	{
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.bytes = bytes;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(String fileSize)
	{
		this.fileSize = fileSize;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public byte[] getBytes()
	{
		return bytes;
	}

	public void setBytes(byte[] bytes)
	{
		this.bytes = bytes;
	}

	@Override
	public String toString()
	{
		return "FileMetadata [fileName=" + fileName + ", fileSize=" + fileSize + ", fileType=" + fileType + "]";
	}

}
