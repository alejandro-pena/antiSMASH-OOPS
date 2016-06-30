package uk.ac.mib.antismashoops.core.model;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class FileMetadata
{
	private static final Logger logger = LoggerFactory.getLogger(FileMetadata.class);

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

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
