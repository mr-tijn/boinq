/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.server.data;

import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.or;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.server.security.ViewsUserDetails;
import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;


@Transactional("views")
public class ViewsDao {
	private SessionFactory sessionFactory;
	@Transactional("views")
	public ViewsUserDetails findUserDetails(String userName) {
		//List<ViewsUserDetails> results = (List<ViewsUserDetails>) super.find("from ViewsUserDetails u where u.username = ?",new Object[]{userName});
		List<ViewsUserDetails> results = (List<ViewsUserDetails>) getSessionFactory().getCurrentSession().createQuery("from ViewsUserDetails u where u.username = :userName").setString("userName", userName).list();
		if (results.size() != 1) return null;
		return results.get(0);
	}
	public ViewsUserDetails getCurrentUser() {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return findUserDetails(userName);
	}
	public void logout() {
		SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
	}
	public void addUser(ViewsUserDetails newUser) {
		getSessionFactory().getCurrentSession().saveOrUpdate(newUser);
	}
	@Transactional("views")
	public List<FeatureDatasource> getPublicDatasources() {
		return getSessionFactory().getCurrentSession().createCriteria(FeatureDatasource.class).add(eq("isPublic",true)).list();
	}
	@Transactional("views")
	public List<FeatureDatasource> getDatasourcesByUser(String userName) {
		return getSessionFactory().getCurrentSession().createCriteria(FeatureDatasource.class).add(eq("owner",userName)).list();
	}
	@Transactional("views")
	public BrowserPerspective getDefaultPerspective(String userName) throws Exception {
		List<BrowserPerspective> defaultPerspectives = getSessionFactory().getCurrentSession().createCriteria(BrowserPerspective.class).add(and(eq("owner", userName),eq("isDefault",true))).list();
		if (defaultPerspectives.size() > 1) {
			throw new Exception("Multiple default perspectives for "+userName);
		}
		else if (defaultPerspectives.size() == 1) {
			return defaultPerspectives.get(0);
		}
		else {
			// system default
			return (BrowserPerspective) getSessionFactory().getCurrentSession().createCriteria(BrowserPerspective.class).add(and(eq("owner", "system"),eq("isDefault",true))).list().get(0);
		}
	}
	@Transactional("views")
	public List<BrowserPerspective> getPerspectivesByUser(String userName) {
		return getSessionFactory().getCurrentSession().createCriteria(BrowserPerspective.class).add(eq("owner", userName)).list();
	}
	@Transactional("views")
	public List<RegionOfInterest> getRegionsOfInterestByUser(String userName) {
		return getSessionFactory().getCurrentSession().createCriteria(RegionOfInterest.class).add(or(eq("owner", userName),eq("isPublic", true))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	@Transactional("views")
	public void updateRegionOfInterest(String userName, RegionOfInterest roi) {
		if (roi.getOwner().equals(userName)) {
			for (GenomicRegion region: roi.getRegions()) {
				getSessionFactory().getCurrentSession().saveOrUpdate(region);
			}
			getSessionFactory().getCurrentSession().saveOrUpdate(roi);
		}
	}
	@Transactional("views")
	public void removeRegionOfInterest(Long roiId) {
		List<RegionOfInterest> result = (List<RegionOfInterest>) getSessionFactory().getCurrentSession().createCriteria(RegionOfInterest.class).add(eq("id",roiId)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		if (result != null && result.size() == 1) {
			RegionOfInterest roi = result.get(0);
			if (roi.getOwner().equals(getCurrentUser().getUsername())) {
				getSessionFactory().getCurrentSession().delete(roi);
			}
		}
	}
	public void removerRegionFromRoi(Long regionId, Long roiId) {
		List<RegionOfInterest> result = (List<RegionOfInterest>) getSessionFactory().getCurrentSession().createCriteria(RegionOfInterest.class).add(eq("id",roiId)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		if (result != null && result.size() == 1) {
			RegionOfInterest roi = result.get(0);
			if (roi.getOwner().equals(getCurrentUser().getUsername())) {
				for (GenomicRegion region: roi.getRegions()) {
					if (regionId == region.getId()) {
						getSessionFactory().getCurrentSession().delete(region);
						roi.getRegions().remove(region);
						getSessionFactory().getCurrentSession().update(roi);
						break;
					}
			}
		}
	}
		
	}

	@Transactional("views")
	public List<TrackSpecification> getTrackSpecsByUser(String userName) {
		return getSessionFactory().getCurrentSession().createCriteria(TrackSpecification.class).add(or(eq("owner",userName),eq("isPublic",true))).list();
	}
	@Transactional("views")
	public List<Analysis> getAnalyses() {
		return getSessionFactory().getCurrentSession().createCriteria(Analysis.class).add(or(eq("owner",getCurrentUser().getUsername()),eq("isPublic",true))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	//FIXME: dirty methods
	@Transactional("views")
	public <T extends Object> T getById(Class<T> clazz, Object id) {
		return (T) getSessionFactory().getCurrentSession().createCriteria(clazz).add(eq("id",id)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	@Transactional("views")
	public <T extends Object> T merge(T arg) {
		return (T) getSessionFactory().getCurrentSession().merge(arg);
	}
	@Transactional("views")
	public <T extends Object> T save(T arg) {
		return (T) getSessionFactory().getCurrentSession().save(arg);
	}
	@Transactional("views")
	public void saveOrUpdate(Object arg) {
		getSessionFactory().getCurrentSession().saveOrUpdate(arg);
	}
	@Transactional("views")
	public void update(Object arg) {
		getSessionFactory().getCurrentSession().update(arg);
	}
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
