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
package com.genohm.viewsGWT.client.util;

public class ScreenRegion {
	protected int visibleStart;
	protected int visibleEnd;
	protected int start;
	protected int end;
	public ScreenRegion() {}
	public ScreenRegion(int start, int end) {
		this.visibleStart = start;
		this.visibleEnd = end;
	}
	public int getDrawStart() {
		// draw in wider region than visible screen in order to have features to DRAG_MOVE
		return visibleStart - getVisibleWidth();
	}
	public int getDrawEnd() {
		return visibleEnd + getVisibleWidth();
	}
	public int getVisibleStart() {
		return visibleStart;
	}
	public void setVisibleStart(int visibleStart) {
		this.visibleStart = visibleStart;
	}
	public int getVisibleEnd() {
		return visibleEnd;
	}
	public void setVisibleEnd(int visibleEnd) {
		this.visibleEnd = visibleEnd;
	}
	public int getVisibleWidth() {
		return 1 + visibleEnd - visibleStart;
	}
	public Boolean equals(ScreenRegion region) {
		return (visibleStart==region.visibleStart && visibleEnd==region.visibleEnd);
	}
}
