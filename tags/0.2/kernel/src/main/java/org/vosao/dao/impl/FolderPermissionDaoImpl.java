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

package org.vosao.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.vosao.dao.DaoFilter;
import org.vosao.dao.FolderPermissionDao;
import org.vosao.entity.FolderPermissionEntity;

/**
 * @author Alexander Oleynik
 */
public class FolderPermissionDaoImpl 
		extends BaseDaoImpl<Long, FolderPermissionEntity> 
		implements FolderPermissionDao {

	public FolderPermissionDaoImpl() {
		super(FolderPermissionEntity.class);
	}

	@Override
	public List<FolderPermissionEntity> selectByFolder(final String folderId) {
		String query = "select from " 
				+ FolderPermissionEntity.class.getName()
				+ " where folderId == pFolderId"
				+ " parameters String pFolderId";
		return select(query, params(folderId));
		/*return select(new DaoFilter<FolderPermissionEntity>() {
			@Override
			public boolean inResult(FolderPermissionEntity entity) {
				return entity.getFolderId().equals(folderId);
			}
		});*/
	}

	@Override
	public FolderPermissionEntity getByFolderGroup(final String folderId, 
			final Long groupId) {
		String query = "select from " + FolderPermissionEntity.class.getName()
				+ " where folderId == pFolderId && groupId == pGroupId"
				+ " parameters String pFolderId, Long pGroupId";
		return selectOne(query, params(folderId, groupId));
		/*return selectOne(new DaoFilter<FolderPermissionEntity>() {
			@Override
			public boolean inResult(FolderPermissionEntity entity) {
				return entity.getFolderId().equals(folderId)
					&& entity.getGroupId().equals(groupId);
			}
		});*/
	}

	private List<FolderPermissionEntity> selectByGroup(final Long groupId) {
		String query = "select from " + FolderPermissionEntity.class.getName()
				+ " where groupId == pGroupId"
				+ " parameters Long pGroupId";
		return select(query, params(groupId));
		/*return select(new DaoFilter<FolderPermissionEntity>() {
			@Override
			public boolean inResult(FolderPermissionEntity entity) {
				return entity.getGroupId().equals(groupId);
			}
		});*/
	}

	@Override
	public void removeByGroup(List<Long> groupIds) {
		for (Long groupId : groupIds) {
			List<FolderPermissionEntity> list = selectByGroup(groupId);
			remove(getIds(list));
		}
	}

	private List<Long> getIds(List<FolderPermissionEntity> list) {
		List<Long> result = new ArrayList<Long>();
		for (FolderPermissionEntity e : list) {
			result.add(e.getId());
		}
		return result;
	}

}
