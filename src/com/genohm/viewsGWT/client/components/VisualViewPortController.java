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
package com.genohm.viewsGWT.client.components;


import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.renderer.GeneRenderer;
import com.genohm.viewsGWT.client.util.ScreenRegion;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.types.LinePattern;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawPath;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;
import com.smartgwt.client.widgets.drawing.events.DragMove;
import com.smartgwt.client.widgets.drawing.events.DragMoveHandler;
import com.smartgwt.client.widgets.drawing.events.DragStart;
import com.smartgwt.client.widgets.drawing.events.DragStartHandler;
import com.smartgwt.client.widgets.drawing.events.DragStop;
import com.smartgwt.client.widgets.drawing.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;

public class VisualViewPortController extends Canvas {

	protected final String selectZoneColor = "cyan";
	
	protected ViewPort viewPort;
	
	protected DrawRect startHandle = null;
	protected DrawRect endHandle = null;
	protected DrawRect activeZone = null;
	
	protected final int horizMargin = 5;
	protected final int vertMargin = 5;
	protected final int handleWidth = 4;
	protected final int handleOversize = 2;
 	
	protected GeneRenderer geneRenderer;
	protected DrawPane drawPaneBG;
	protected DrawPane drawPaneFG;
	
	public VisualViewPortController(ViewPort viewPort) {
		//TODO: set backgroundrenderer
		this.viewPort = viewPort;
		setHeight(50);
		setWidth100();
		addDrawPane();
		RendererSettings settings = new RendererSettings();
		settings.setFeatureColor("grey");
		settings.setFeatureHeight(50);
		settings.setGapWidth(0);
		geneRenderer = new GeneRenderer(drawPaneBG, viewPort.getContextScaler(), null, settings);
		subscribe();

	}
	
	protected void addDrawPane() {
		drawPaneBG = new DrawPane();
		drawPaneBG.setWidth100();
		drawPaneBG.setHeight(50);
		drawPaneBG.addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				try {
					drawDrawPaneBG();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		addChild(drawPaneBG);				// apparently the addChild causes the drawPane to draw when the Canvas draws
		drawPaneFG = new DrawPane();
		drawPaneFG.setWidth100();
		drawPaneFG.setHeight(50);
		drawPaneFG.addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				try {
					drawDrawPaneFG();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		drawPaneFG.setCanDrag(true); 		// necessary to make dragHandlers work on children
		addChild(drawPaneFG);
	}
	
	
	
	protected void subscribe() {
		EventBus.subscribe(EventbusTopic.VIEWPORT_CHANGED, new TopicSubscriber<ViewPort>() {
			@Override
			public void onEvent(Subscription subscription, ViewPort event) {
				setHorizontalPositions();
			}
			@Override
			public int getId() {
				return 0;
			}
			
		});
		EventBus.subscribe(EventbusTopic.VIEWCONTEXT_CHANGED, new TopicSubscriber<ViewPort>() {
			@Override
			public void onEvent(Subscription subscription, ViewPort event) {
				setHorizontalPositions();
			}
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.SELECTED_FEATURE_FETCHED, new TopicSubscriber<Feature>() {

			@Override
			public void onEvent(Subscription subscription, Feature selectedFeature) {
				//render selected feature
				if (selectedFeature instanceof Gene) {
						try {
							drawDrawPaneBG();
							setHorizontalPositions();
						} catch (Exception e) {
							SC.logWarn("ERROR during drawing of visualviewportcontroller : " + e.getMessage());
						}
				}
			}

			@Override
			public int getId() {
				return 0;
			}
			
		});
	}
	
	public void drawDrawPaneBG() throws Exception {
		drawPaneBG.destroyItems();
		drawBackGround();
	}
		
	public void drawDrawPaneFG() throws Exception {
		drawPaneFG.destroyItems();
		drawDraggableZone();
	}

	protected void drawBackGround() throws Exception {
		if (viewPort.getFocusedFeature() == null) {
			final int numberOfWobbles = 21;
			float wobbleWidth = (drawPaneBG.getInnerWidth() - 2 * horizMargin)/(float) numberOfWobbles;
			int wobbleHeight = (drawPaneBG.getInnerHeight() - 2 * vertMargin);
			int down = 1;
			DrawPath wobble = new DrawPath();
			wobble.setDrawPane(drawPaneBG);
			Point[] points = new Point[numberOfWobbles];
			for (int i = 0; i < numberOfWobbles; i++) {
				points[i] = new Point(Math.round(horizMargin + i * wobbleWidth),vertMargin + down * wobbleHeight);
				down = 1-down;
			}
			wobble.setPoints(points);
			wobble.setLineWidth(3);
			wobble.draw(); //FIXME: already drawn
		} else if (viewPort.getFocusedFeature() instanceof Gene) {
			viewPort.getContextScaler().setScreenRegion(new ScreenRegion(0,drawPaneBG.getInnerWidth()));
			geneRenderer.createDrawFeature(viewPort.getFocusedFeature(),getInnerContentHeight(),0).draw();
			//TODO: using visitor pattern this should become focusedFeature.render(drawPane)
		}
	}

	int xprevious = 0;
	DragStartHandler initPrevious = new DragStartHandler() {
		@Override
		public void onDragStart(DragStart event) {
			xprevious = event.getX();			
		}
	};

	protected void drawDraggableZone() {
		startHandle = drawHandle(viewPort.getMainGenomicRegion().getRegionStart());
		startHandle.setCanDrag(true);
		startHandle.addDragStartHandler(initPrevious);
		startHandle.addDragMoveHandler(new DragMoveHandler() {
			@Override
			public void onDragMove(DragMove event) {
				if (event.getX() > endHandle.getLeft()) return;
				startHandle.moveTo(startHandle.getLeft(),-drawPaneBG.getInnerHeight()/2);
				int dX = event.getX() - xprevious;
				activeZone.moveBy(dX, 0);
				activeZone.resizeBy(-dX, 0);
				xprevious = event.getX();
			}
		});
		startHandle.addDragStopHandler(new DragStopHandler() {
			@Override
			public void onDragStop(DragStop event) {
				Long startValue = getValue(event.getX());
				viewPort.setGenomicStart(startValue);
			}
		});

		endHandle = drawHandle(viewPort.getMainGenomicRegion().getRegionEnd());
		endHandle.setCanDrag(true);
		endHandle.addDragStartHandler(initPrevious);
		endHandle.addDragMoveHandler(new DragMoveHandler() {
			@Override
			public void onDragMove(DragMove event) {
				if (event.getX() < startHandle.getLeft() + startHandle.getWidth()) return;
				endHandle.moveTo(endHandle.getLeft(),-drawPaneBG.getInnerHeight()/2);
				int dX = event.getX() - xprevious;
				activeZone.resizeBy(dX, 0);
				xprevious = event.getX();
			}
		});
		endHandle.addDragStopHandler(new DragStopHandler() {
			@Override
			public void onDragStop(DragStop event) {
				Long endValue = getValue(event.getX());
				viewPort.setGenomicEnd(endValue);
				//TODO: autoswap strand
				//				if (endValue < viewPort.getGenomicStart()) viewPort.getMainGenomicRegion().setStrand(false);
				//				else viewPort.getMainGenomicRegion().setStrand(true);
			}
		});

		activeZone = drawSelectedZone(); //FIXME: throws already drawn
		activeZone.setCanDrag(true);
		activeZone.addDragStartHandler(initPrevious);
		activeZone.addDragMoveHandler(new DragMoveHandler() {
			@Override
			public void onDragMove(DragMove event) {
				activeZone.moveTo(activeZone.getLeft(),-drawPaneBG.getInnerHeight()/2);
				int dX = event.getX() - xprevious;
				startHandle.moveBy(dX, 0);
				endHandle.moveBy(dX, 0);
				xprevious = event.getX();
//				if (viewPort.getMainGenomicRegion().getStrand().equals(viewPort.getContextGenomicRegion().getStrand())) {
//					EventBus.publish(new Event(EventbusTopic.DRAG_MOVE), -dX * viewPort.getMainGenomicRegion().getWidth() / viewPort.getContextGenomicRegion().getWidth());
//				} else {
//					EventBus.publish(new Event(EventbusTopic.DRAG_MOVE), dX * viewPort.getMainGenomicRegion().getWidth() / viewPort.getContextGenomicRegion().getWidth());
//				}
			}
		});
		activeZone.addDragStopHandler(new DragStopHandler() {
			@Override
			public void onDragStop(DragStop event) {
				Long startValue = getValue(startHandle.getCenter()[0]);
				Long endValue = getValue(endHandle.getCenter()[0]);
				GenomicRegion newRegion = viewPort.getMainGenomicRegion();
				newRegion.setVisibleStart(startValue);
				newRegion.setVisibleEnd(endValue);
				viewPort.setMainGenomicRegion(newRegion);
			}
		});
		
	}
	
	protected int getPosition(Long value) {
		int minPosition = horizMargin;
		int maxPosition = drawPaneBG.getInnerWidth() - horizMargin;
		int positionRange = maxPosition - minPosition;
		double fraction = ((double) (value - viewPort.getContextGenomicRegion().getVisibleStart()) / (double) (viewPort.getContextGenomicRegion().getVisibleEnd() - viewPort.getContextGenomicRegion().getVisibleStart()));
		//return Math.max(minPosition, Math.min(maxPosition, minPosition + (int) (fraction * positionRange)));
		return minPosition + (int) (fraction * positionRange);
	}
	
	protected Long getValue(int position) {
		int minPosition = horizMargin;
		int maxPosition = drawPaneBG.getInnerWidth() - horizMargin;
		//position = Math.min(position, maxPosition);
		//position = Math.max(position, minPosition);
		int positionRange = maxPosition - minPosition;
		double fraction = (double) (position - minPosition) / (double) (positionRange);
		return viewPort.getContextGenomicRegion().getVisibleStart() + (long) (fraction * (viewPort.getContextGenomicRegion().getVisibleEnd() - viewPort.getContextGenomicRegion().getVisibleStart()));
	}
	
	protected DrawRect drawHandle(Long position) {
		int centerPosition = getPosition(position);
 		DrawRect handle = new DrawRect();
		handle.setDrawPane(drawPaneFG);
		handle.setWidth(handleWidth);
		handle.setHeight(2*drawPaneFG.getInnerHeight());
		handle.setCenter(centerPosition, drawPaneFG.getInnerHeight()/2);
		handle.setFillColor(selectZoneColor);
		handle.setFillOpacity((float) .5);
		handle.setLineWidth(1);
		handle.setLineColor(selectZoneColor);
		handle.draw(); //FIXME: throws already drawn
		return handle;
	}

	protected DrawRect drawSelectedZone() {
		int start = getPosition(viewPort.getMainGenomicRegion().getVisibleStart());
		int end = getPosition(viewPort.getMainGenomicRegion().getVisibleEnd());
		int left = Math.min(start, end);
		int right = Math.max(start, end);
		int width = right - left;
		if (width > handleWidth) {
			left += handleWidth/2;
			right -= handleWidth/2;
			width -= handleWidth;
		}
		DrawRect zone = new DrawRect();
		zone.setDrawPane(drawPaneFG);
		zone.setLeft(left);
		zone.setWidth(width);
		zone.setHeight(2*drawPaneFG.getInnerHeight());
		zone.setLineWidth(0);
		zone.setFillColor(selectZoneColor);
		zone.setFillOpacity((float) .1);
		zone.setCanDrag(true);
		zone.draw(); //FIXME: throws already drawn
		return zone;
	}
	
	protected void setHorizontalPositions() {
		int startPos = getPosition(viewPort.getMainGenomicRegion().getVisibleStart());
		int endPos = getPosition(viewPort.getMainGenomicRegion().getVisibleEnd());
		startHandle.moveTo(startPos - handleWidth/2, startHandle.getTop());
		endHandle.moveTo(endPos - handleWidth/2, endHandle.getTop());

		int left = Math.min(startPos, endPos);
		int right = Math.max(startPos, endPos);
		int width = right - left;
		if (width > handleWidth) {
			left += handleWidth/2;
			right -= handleWidth/2;
			width -= handleWidth;
		}

		activeZone.moveTo(left, 0);
		activeZone.resizeTo(width, activeZone.getHeight());
	}

}
