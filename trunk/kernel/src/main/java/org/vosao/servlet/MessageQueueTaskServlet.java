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

package org.vosao.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vosao.business.mq.Message;
import org.vosao.business.mq.TopicSubscriber;
import org.vosao.common.VosaoContext;
import org.vosao.entity.helper.UserHelper;
import org.vosao.utils.StreamUtil;

import com.google.appengine.repackaged.com.google.common.util.Base64;

/**
 * 
 * @author Alexander Oleynik
 *
 */
public class MessageQueueTaskServlet extends AbstractServlet {

	public static final String MQ_URL = "/_ah/queue/mq";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String topic = request.getParameter("topic");
		if (topic == null) {
			logger.error("Topic is null");
			return;
		}
		try {
			Message message = (Message)StreamUtil.toObject(
					Base64.decode(request.getParameter("message")));
			logger.info("MQ: " + topic + " " + message);
			VosaoContext.getInstance().setUser(UserHelper.ADMIN);
			for (TopicSubscriber subscriber : getMessageQueue()
				.getSubscribers(topic)) {
				subscriber.onMessage(message);
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}