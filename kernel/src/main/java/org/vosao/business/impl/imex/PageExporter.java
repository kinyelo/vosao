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

package org.vosao.business.impl.imex;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.vosao.business.Business;
import org.vosao.business.decorators.TreeItemDecorator;
import org.vosao.business.impl.imex.dao.DaoTaskAdapter;
import org.vosao.dao.Dao;
import org.vosao.dao.DaoTaskException;
import org.vosao.entity.CommentEntity;
import org.vosao.entity.ContentEntity;
import org.vosao.entity.FolderEntity;
import org.vosao.entity.LanguageEntity;
import org.vosao.entity.PageEntity;
import org.vosao.entity.StructureEntity;
import org.vosao.entity.StructureTemplateEntity;
import org.vosao.entity.TemplateEntity;
import org.vosao.enums.PageState;
import org.vosao.enums.PageType;
import org.vosao.utils.DateUtil;
import org.vosao.utils.StrUtil;
import org.vosao.utils.XmlUtil;

public class PageExporter extends AbstractExporter {

	private static final Log logger = LogFactory.getLog(PageExporter.class);

	private ResourceExporter resourceExporter;
	private ConfigExporter configExporter;
	private FormExporter formExporter;
	private UserExporter userExporter;
	private PagePermissionExporter pagePermissionExporter;
	
	public PageExporter(Dao aDao, Business aBusiness, 
			DaoTaskAdapter daoTaskAdapter) {
		super(aDao, aBusiness, daoTaskAdapter);
		resourceExporter = new ResourceExporter(aDao, aBusiness, daoTaskAdapter);
		configExporter = new ConfigExporter(aDao, aBusiness, daoTaskAdapter);
		formExporter = new FormExporter(aDao, aBusiness, daoTaskAdapter);
		userExporter = new UserExporter(aDao, aBusiness, daoTaskAdapter);
		pagePermissionExporter = new PagePermissionExporter(aDao, aBusiness,
				daoTaskAdapter);
	}
	
	private void createPageXML(TreeItemDecorator<PageEntity> page,
			Element root) {
		Element pageElement = root.addElement("page"); 
		createPageDetailsXML(page.getEntity(), pageElement);
		createPageVersionXML(page.getEntity(), pageElement);
		createCommentsXML(page, pageElement);
		for (TreeItemDecorator<PageEntity> child : page.getChildren()) {
			createPageXML(child, pageElement);
		}
		pagePermissionExporter.createPagePermissionsXML(pageElement, 
				page.getEntity().getFriendlyURL());
	}
	
	private void createPageVersionXML(PageEntity page, Element pageElement) {
		List<PageEntity> versions = getDao().getPageDao().selectByUrl(
				page.getFriendlyURL());
		for (PageEntity pageVersion : versions) {
			if (!pageVersion.getId().equals(page.getId())) {
				createPageDetailsXML(pageVersion, pageElement.addElement(
						"page-version"));
			}
		}
	}

	private static String packTitle(PageEntity page) {
		StringBuffer b = new StringBuffer("<title>");
		b.append(page.getTitleValue()).append("</title>");
		return b.toString();
	}

	private static String unpackTitle(String xml) {
		if (!xml.startsWith("<title>")) {
			return "en" + xml;
		}
		return xml.replace("<title>", "").replace("</title>", "");
	}
	
	private void createPageDetailsXML(PageEntity page, Element pageElement) {
		pageElement.addAttribute("url", page.getFriendlyURL());
		pageElement.addAttribute("title", packTitle(page));
		pageElement.addAttribute("commentsEnabled", String.valueOf(
				page.isCommentsEnabled()));
		if (page.getPublishDate() != null) {
			pageElement.addAttribute("publishDate", 
				DateUtil.toString(page.getPublishDate()));
		}
		TemplateEntity template = getDao().getTemplateDao().getById(
				page.getTemplate());
		if (template != null) {
			pageElement.addAttribute("theme", template.getUrl());
		}
		pageElement.addElement("version").setText(page.getVersion().toString());
		pageElement.addElement("versionTitle").setText(page.getVersionTitle());
		pageElement.addElement("state").setText(page.getState().name());
		String createUserId = "1";
		pageElement.addElement("createUserEmail").setText(
				page.getCreateUserEmail());
		pageElement.addElement("modUserId").setText(
				page.getModUserEmail());
		if (page.getCreateDate() != null) {
			pageElement.addElement("createDate").setText(
					DateUtil.dateTimeToString(page.getCreateDate()));
		}
		if (page.getModDate() != null) {
			pageElement.addElement("modDate").setText(
					DateUtil.dateTimeToString(page.getModDate()));
		}
		StructureEntity structure = getDao().getStructureDao().getById(
				page.getStructureId());
		pageElement.addElement("structure").setText(
				structure == null ? "" : structure.getTitle());
		StructureTemplateEntity structureTemplate = getDao()
				.getStructureTemplateDao().getById(page.getStructureTemplateId());
		pageElement.addElement("structureTemplate").setText(
				structureTemplate == null ? ""	: structureTemplate.getTitle());
		pageElement.addElement("pageType").setText(page.getPageType().name());
		List<ContentEntity> contents = getDao().getPageDao().getContents(
				page.getId()); 
		for (ContentEntity content : contents) {
			Element contentElement = pageElement.addElement("content");
			contentElement.addAttribute("language", content.getLanguageCode());
			contentElement.addText(content.getContent());
		}
	}
	
	private void createCommentsXML(TreeItemDecorator<PageEntity> page, 
			Element pageElement) {
		Element commentsElement = pageElement.addElement("comments");
		List<CommentEntity> comments = getDao().getCommentDao().getByPage(
				page.getEntity().getFriendlyURL());
		for (CommentEntity comment : comments) {
			Element commentElement = commentsElement.addElement("comment");
			commentElement.addAttribute("name", comment.getName());
			commentElement.addAttribute("disabled", String.valueOf(
					comment.isDisabled()));
			commentElement.addAttribute("publishDate", 
				DateUtil.dateTimeToString(comment.getPublishDate()));
			commentElement.setText(comment.getContent());
		}
	}

	public void createPagesXML(Element siteElement) {
		Element pages = siteElement.addElement("pages");
		TreeItemDecorator<PageEntity> pageRoot = getBusiness()
				.getPageBusiness().getTree();
		createPageXML(pageRoot, pages);
	}
	
	public void addContentResources(final ZipOutputStream out)
			throws IOException {
		TreeItemDecorator<FolderEntity> root = getBusiness()
				.getFolderBusiness().getTree();
		TreeItemDecorator<FolderEntity> folder = getBusiness()
				.getFolderBusiness().findFolderByPath(root, "/page");
		if (folder == null) {
			return;
		}
		resourceExporter.addResourcesFromFolder(out, folder, "page/");
	}

	public void readPages(Element pages) throws DaoTaskException {
		for (Iterator<Element> i = pages.elementIterator(); i.hasNext(); ) {
			Element pageElement = i.next();
			readPage(pageElement, null);
		}
	}

	private void readPage(Element pageElement, PageEntity parentPage) 
			throws DaoTaskException {
		PageEntity page = readPageVersion(pageElement);
		for (Iterator<Element> i = pageElement.elementIterator(); i.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("page")) {
				readPage(element, page);
			}
			if (element.getName().equals("comments")) {
				readComments(element, page);
			}
			if (element.getName().equals("page-version")) {
				readPageVersion(element);
			}
			if (element.getName().equals("permissions")) {
				pagePermissionExporter.readPagePermissions(element, 
						page.getFriendlyURL());
			}
		}
	}

	private PageEntity readPageVersion(Element pageElement) 
			throws DaoTaskException {
		String title = unpackTitle(pageElement.attributeValue("title"));
		String url = pageElement.attributeValue("url");
		String themeUrl = pageElement.attributeValue("theme");
		String commentsEnabled = pageElement.attributeValue("commentsEnabled");
		Date publishDate = new Date();
		if (pageElement.attributeValue("publishDate") != null) {
			try {
				publishDate = DateUtil.toDate(pageElement
						.attributeValue("publishDate"));
			} catch (ParseException e) {
				logger.error("Wrong date format "
						+ pageElement.attributeValue("publishDate") + " "
						+ title);
			}
		}
		TemplateEntity template = getDao().getTemplateDao().getByUrl(themeUrl);
		String templateId = null;
		if (template != null) {
			templateId = template.getId();
		}
		PageEntity newPage = new PageEntity();
		newPage.setTitleValue(title);
		newPage.setFriendlyURL(url);
		newPage.setTemplate(templateId);
		newPage.setPublishDate(publishDate);
		if (commentsEnabled != null) {
			newPage.setCommentsEnabled(Boolean.valueOf(commentsEnabled));
		}
		newPage.setState(PageState.APPROVED);
		for (Iterator<Element> i = pageElement.elementIterator(); i.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("version")) {
				newPage.setVersion(XmlUtil.readIntegerText(element, 1));
			}
			if (element.getName().equals("versionTitle")) {
				newPage.setVersionTitle(element.getText());
			}
			if (element.getName().equals("state")) {
				newPage.setState(PageState.valueOf(element.getText()));
			}
			if (element.getName().equals("createUserEmail")) {
				newPage.setCreateUserEmail(element.getText());
			}
			if (element.getName().equals("modUserEmail")) {
				newPage.setModUserEmail(element.getText());
			}
			if (element.getName().equals("pageType")) {
				newPage.setPageType(PageType.valueOf(element.getText()));
			}
			if (element.getName().equals("structure")) {
				StructureEntity structure = getDao().getStructureDao().getByTitle(
						element.getText());
				newPage.setStructureId(structure == null ? "" : structure.getId());
			}
			if (element.getName().equals("structureTemplate")) {
				StructureTemplateEntity structureTemplate = getDao()
						.getStructureTemplateDao().getByTitle(element.getText());
				newPage.setStructureTemplateId(structureTemplate == null ? "" : 
						structureTemplate.getId());
			}
			if (element.getName().equals("createDate")) {
				try {
					newPage.setCreateDate(DateUtil.dateTimeToDate(
							element.getText()));
				} catch (ParseException e) {
					logger.error("Wrong date format for createDate " 
							+ element.getText());
				}
			}
			if (element.getName().equals("modDate")) {
				try {
					newPage.setModDate(DateUtil.dateTimeToDate(
							element.getText()));
				} catch (ParseException e) {
					logger.error("Wrong date format for createDate " 
							+ element.getText());
				}
			}
		}
		PageEntity page = getDao().getPageDao().getByUrlVersion(url, 
				newPage.getVersion());
		if (page != null) {
			page.copy(newPage);
		} else {
			page = newPage;
		}
		getDaoTaskAdapter().pageSave(page);
		readContents(pageElement, page);
		return page;
	}
	
	private void readContents(Element pageElement, PageEntity page) 
			throws DaoTaskException {
		for (Iterator<Element> i = pageElement.elementIterator(); i.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("content")) {
				String content = element.getText();
				String language = element.attributeValue("language");
				if (language == null) {
					language = LanguageEntity.ENGLISH_CODE;
				}
				getDaoTaskAdapter().setPageContent(page.getId(), language, 
						content);
			}
		}
	}
	
	private void readComments(Element commentsElement, PageEntity page) 
			throws DaoTaskException {
		for (Iterator<Element> i = commentsElement.elementIterator(); i
				.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("comment")) {
				String name = element.attributeValue("name");
				Date publishDate = new Date();
				try {
					publishDate = DateUtil.dateTimeToDate(element
							.attributeValue("publishDate"));
				} catch (ParseException e) {
					logger.error("Error parsing comment publish date "
							+ element.attributeValue("publishDate"));
				}
				boolean disabled = Boolean.valueOf(element
						.attributeValue("disabled"));
				String content = element.getText();
				CommentEntity comment = new CommentEntity(name, content,
						publishDate, page.getId(), disabled);
				getDaoTaskAdapter().commentSave(comment);
			}
		}
	}
	
}