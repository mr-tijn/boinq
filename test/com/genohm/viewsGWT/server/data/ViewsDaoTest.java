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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.server.security.Authority;
import com.genohm.viewsGWT.server.security.ViewsUserDetails;
import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;

public class ViewsDaoTest {
	
	private static Logger log = Logger.getLogger(ViewsDaoTest.class);
	protected static ViewsDao viewsDao;
	
	@BeforeClass
	public static void setUp() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		viewsDao = (ViewsDao) context.getBean("viewsDao");
	}

	@Test
	public void testGetPublicDatasources() {
		List<FeatureDatasource> result = viewsDao.getPublicDatasources();
		assert(result.size()>1);
	}
	
	@Test
	public void testGetDatasourcesByUser() {
		List<FeatureDatasource> result = viewsDao.getDatasourcesByUser("martijn");
		assert(result.size() > 1);
	}
	@Test
	@Transactional
	public void testAddUser() {
		ViewsUserDetails newUser = new ViewsUserDetails();
//		newUser.setUsername("admin");
//		PasswordEncoder encoder = new Md5PasswordEncoder();
//	    String hashedPass = encoder.encodePassword("gbbakje", null);
//		newUser.setPassword(hashedPass);
//		newUser = viewsDao.merge(newUser);
//		Set<Authority> authorities = new HashSet<Authority>();
//		Authority userRole = new Authority();
//		userRole.setAuthority("ROLE_USER");
//		userRole.setUserId(newUser.getId());
//		Authority adminRole = new Authority();
//		adminRole.setAuthority("ROLE_AUTHORITY");
//		adminRole.setUserId(newUser.getId());
//		authorities.add(userRole);
//		authorities.add(adminRole);
//		newUser.setAuthorities(authorities);
//		viewsDao.merge(newUser);
		newUser.setUsername("nettab");
		PasswordEncoder encoder = new Md5PasswordEncoder();
	    String hashedPass = encoder.encodePassword("2013", null);
		newUser.setPassword(hashedPass);
		newUser = viewsDao.merge(newUser);
		Set<Authority> authorities = new HashSet<Authority>();
		Authority userRole = new Authority();
		userRole.setAuthority("ROLE_USER");
		userRole.setUserId(newUser.getId());
		authorities.add(userRole);
		newUser.setAuthorities(authorities);
		viewsDao.merge(newUser);

	}
	
	
	void testAddPerspective(ViewsUserDetails user) {
		List<BrowserPerspective> perspectives = viewsDao.getPerspectivesByUser(user.getUsername());
		BrowserPerspective newPerspective = new BrowserPerspective();
		newPerspective.setOwner(user.getUsername());
		newPerspective.setIsDefault(true);
		newPerspective.setIsPublic(false);
		List<TrackSpecification> trackSpecs = newPerspective.getTracks();
		TrackSpecification newTrackSpec = new TrackSpecification();
		List<TrackSpecification> availableTrackSpecs = viewsDao.getTrackSpecsByUser(user.getUsername());
		
	}
	
	@Test
	public void testUpdateROI() {
//		RegionOfInterest roi = new RegionOfInterest();
//		roi.setIsPublic(true);
//		roi.setName("some kinases");
//		roi.setOwner("martijn");
//		roi.setRank(0);
//		List<GenomicRegion> regions = new LinkedList<GenomicRegion>();
//		GenomicRegion region1 = new GenomicRegion(233457388L,233525122L,"1",Species.HUMAN,true);
//		regions.add(region1);
//		roi.setRegions(regions);
		List<RegionOfInterest> regions = viewsDao.getRegionsOfInterestByUser("martijn");
		RegionOfInterest roi = regions.get(0);
		roi.getRegions().add(new GenomicRegion("someregion",100000L, 100100L, "1", Species.HUMAN, true));
		viewsDao.updateRegionOfInterest("martijn", roi);
	}
	
	@Test
	public void testGetROI() {
		List<RegionOfInterest> regions = viewsDao.getRegionsOfInterestByUser("martijn");	
	}
	
	@Test
	public void generatePassword() {
		PasswordEncoder encoder = new Md5PasswordEncoder();
	    String hashedPass = encoder.encodePassword("2012", null);
	    System.out.println(hashedPass);
	}

}
