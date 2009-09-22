package org.vosao.entity;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FileEntity implements Serializable {

	private static final long serialVersionUID = 4L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
	
	@Persistent
	private String title;
	
	@Persistent
	private String filename;	

	@Persistent
	private String folderId;	

	@Persistent
	private String mimeType;
    
	@Persistent
	private Date mdtime;
	
	@Persistent
	private int size;

	public FileEntity() {
	}
	
	public FileEntity(String aTitle, String aName, String aFolderId,
			String aMimeType, Date aMdttime, int aSize) {
		this();
		title = aTitle;
		filename = aName;
		folderId = aFolderId;
		mimeType = aMimeType;
		mdtime = aMdttime;
		size = aSize;
	}
	
	public void copy(final FileEntity entity) {
		setTitle(entity.getTitle());
		setFilename(entity.getFilename());
		setFolderId(entity.getFolderId());
		setMdtime(entity.getMdtime());
		setMimeType(entity.getMimeType());
		setSize(entity.getSize());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Date getMdtime() {
		return mdtime;
	}

	public void setMdtime(Date mdtime) {
		this.mdtime = mdtime;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}