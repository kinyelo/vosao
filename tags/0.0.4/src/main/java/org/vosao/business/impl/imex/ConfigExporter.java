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

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.vosao.business.Business;
import org.vosao.dao.Dao;
import org.vosao.entity.ConfigEntity;
import org.vosao.entity.LanguageEntity;
import org.vosao.utils.XmlUtil;

public class ConfigExporter extends AbstractExporter {

	public ConfigExporter(Dao aDao, Business aBusiness) {
		super(aDao, aBusiness);
	}
	
	public void createConfigXML(Element siteElement) {
		Element configElement = siteElement.addElement("config");
		ConfigEntity config = getBusiness().getConfigBusiness().getConfig();
		if (config.getGoogleAnalyticsId() != null) {
			Element googleAnalytics = configElement.addElement("google-analytics");
			googleAnalytics.setText(config.getGoogleAnalyticsId());
		}
		if (config.getSiteEmail() != null) {
			Element siteEmail = configElement.addElement("email");
			siteEmail.setText(config.getSiteEmail());
		}
		if (config.getSiteDomain() != null) {
			Element siteDomain = configElement.addElement("domain");
			siteDomain.setText(config.getSiteDomain());
		}
		if (config.getEditExt() != null) {
			Element editExt = configElement.addElement("edit-ext");
			editExt.setText(config.getEditExt());
		}
		if (config.getRecaptchaPrivateKey() != null) {
			Element recaptcha = configElement.addElement("recaptchaPrivateKey");
			recaptcha.setText(config.getRecaptchaPrivateKey());
		}
		if (config.getRecaptchaPublicKey() != null) {
			Element elem = configElement.addElement("recaptchaPublicKey");
			elem.setText(config.getRecaptchaPublicKey());
		}
		if (config.getCommentsEmail() != null) {
			Element elem = configElement.addElement("commentsEmail");
			elem.setText(config.getCommentsEmail());
		}
		if (config.getCommentsTemplate() != null) {
			Element elem = configElement.addElement("commentsTemplate");
			elem.setText(config.getCommentsTemplate());
		}
		configElement.addElement("enableRecaptcha").setText(
				String.valueOf(config.isEnableRecaptcha()));
		configElement.addElement("version").setText(config.getVersion());
		createLanguagesXML(configElement);
	}

	private void createLanguagesXML(Element configElement) {
		Element languagesElement = configElement.addElement("languages");
		List<LanguageEntity> langs = getDao().getLanguageDao().select();
		for (LanguageEntity lang : langs) {
			Element langElem = languagesElement.addElement("language");
			langElem.addAttribute("code", lang.getCode());
			langElem.addAttribute("title", lang.getTitle());
		}
	}
	
	public void readConfigs(Element configElement) {
		ConfigEntity config = getBusiness().getConfigBusiness().getConfig();
		for (Iterator<Element> i = configElement.elementIterator(); i.hasNext(); ) {
            Element element = i.next();
            if (element.getName().equals("google-analytics")) {
            	config.setGoogleAnalyticsId(element.getText());
            }
            if (element.getName().equals("email")) {
            	config.setSiteEmail(element.getText());
            }
            if (element.getName().equals("domain")) {
            	config.setSiteDomain(element.getText());
            }
            if (element.getName().equals("edit-ext")) {
            	config.setEditExt(element.getText());
            }
            if (element.getName().equals("recaptchaPrivateKey")) {
            	config.setRecaptchaPrivateKey(element.getText());
            }
            if (element.getName().equals("recaptchaPublicKey")) {
            	config.setRecaptchaPublicKey(element.getText());
            }
            if (element.getName().equals("commentsEmail")) {
            	config.setCommentsEmail(element.getText());
            }
            if (element.getName().equals("commentsTemplate")) {
            	config.setCommentsTemplate(element.getText());
            }
            if (element.getName().equals("version")) {
            	config.setVersion(element.getText());
            }
            if (element.getName().equals("languages")) {
            	readLanguages(element);
            }
            if (element.getName().equals("enableRecaptcha")) {
            	config.setEnableRecaptcha(XmlUtil.readBooleanText(
            			element, false));
            }
		}
		getDao().getConfigDao().save(config);
	}
	
	public void readLanguages(Element languagesElement) {
		for (Iterator<Element> i = languagesElement.elementIterator(); i.hasNext(); ) {
            Element element = i.next();
            if (element.getName().equals("language")) {
            	String code = element.attributeValue("code");
            	String title = element.attributeValue("title");
            	LanguageEntity language = getDao().getLanguageDao().getByCode(
            			code);
            	if (language == null) {
                	language = new LanguageEntity(code, title);
                	getDao().getLanguageDao().save(language);
            	}
            }
		}
	}
	
}