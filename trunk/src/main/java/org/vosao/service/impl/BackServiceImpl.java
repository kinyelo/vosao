/**
 * Vosao CMS. Simple CMS for Google App Engine.
 * Copyright (C) 2009 Vosao development team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * email: vosao.dev@gmail.com
 */

package org.vosao.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jabsorb.JSONRPCBridge;
import org.vosao.service.BackService;
import org.vosao.service.back.CommentService;
import org.vosao.service.back.ConfigService;
import org.vosao.service.back.FieldService;
import org.vosao.service.back.FileService;
import org.vosao.service.back.FolderService;
import org.vosao.service.back.FormService;
import org.vosao.service.back.PageService;
import org.vosao.service.back.TemplateService;

public class BackServiceImpl implements BackService {

	private static final Log log = LogFactory.getLog(BackServiceImpl.class);

	private FileService fileService;
	private FolderService folderService;
	private CommentService commentService;
	private PageService pageService;
	private TemplateService templateService;
	private FormService formService;
	private FieldService fieldService;
	private ConfigService configService;
	
	public void init() {
	}
	
	public void register(JSONRPCBridge bridge) {
		bridge.registerObject("fileService", fileService);
		bridge.registerObject("folderService", folderService);
		bridge.registerObject("commentService", commentService);
		bridge.registerObject("pageService", pageService);
		bridge.registerObject("templateService", templateService);
		bridge.registerObject("fieldService", fieldService);
		bridge.registerObject("formService", formService);
		bridge.registerObject("configService", configService);
	}
	
	@Override
	public FileService getFileService() {
		return fileService;
	}

	@Override
	public void setFileService(FileService bean) {
		fileService = bean;
	}

	@Override
	public FolderService getFolderService() {
		return folderService;
	}

	@Override
	public void setFolderService(FolderService bean) {
		folderService = bean;
	}

	@Override
	public CommentService getCommentService() {
		return commentService;
	}

	@Override
	public void setCommentService(CommentService bean) {
		commentService = bean;		
	}

	@Override
	public PageService getPageService() {
		return pageService;
	}

	@Override
	public void setPageService(PageService bean) {
		pageService = bean;		
	}

	@Override
	public TemplateService getTemplateService() {
		return templateService;
	}

	@Override
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Override
	public FieldService getFieldService() {
		return fieldService;
	}

	@Override
	public void setFieldService(FieldService bean) {
		fieldService = bean;		
	}

	@Override
	public ConfigService getConfigService() {
		return configService;
	}

	@Override
	public void setConfigService(ConfigService bean) {
		configService = bean;		
	}

	@Override
	public FormService getFormService() {
		return formService;
	}

	@Override
	public void setFormService(FormService bean) {
		formService = bean;		
	}
	
}