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
package com.genohm.viewsGWT.client.track;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.client.FeatureServerAsync;
import com.genohm.viewsGWT.client.ResourceManager;
import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.renderer.DrawFeature;
import com.genohm.viewsGWT.client.renderer.FeatureRenderer;
import com.genohm.viewsGWT.client.util.FeatureTools;
import com.genohm.viewsGWT.client.util.ScreenRegion;
import com.genohm.viewsGWT.shared.ArgumentMap;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.ZoomLevel;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DragMoveEvent;
import com.smartgwt.client.widgets.events.DragMoveHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

public class Track extends SectionStackSection {

	protected static final int DRAG_SENSITIVITY = 1;

	protected TrackSpecification trackSpec = null;
	protected DrawPane drawPane = null;
	protected ViewPort viewPort = null;
	protected final FeatureRenderer renderer;
	protected final FeatureDatasource featureDS;
	// TODO: remove legacy field featureSource
	protected final String featureSource;
	protected final String filterExpression;
	List<Feature> features = new LinkedList<Feature>();
	List<DrawFeature> drawFeatures = new LinkedList<DrawFeature>();
	protected DragMode dragMode = DragMode.MOVE;
	protected int draggingStart;
	protected int draggingEnd;
	protected int previousX;
	protected ZoomLevel zoomLevel = ZoomLevel.OUTLINE;
	protected DrawRect selectionRect = null;
	protected Boolean MOVING = false;

	public Track(ViewPort viewPort, TrackSpecification spec) {
		super(spec.getTitle());
		trackSpec = spec;
		setResizeable(true);
		setControls(closeTrackButton());
		addDrawPane(trackSpec.getHeight());
		this.viewPort = viewPort;
		this.featureDS = trackSpec.getFeatureDatasource();
		this.featureSource = null;
		this.renderer = FeatureRenderer.getRenderer(
				trackSpec.getRendererSettings(), drawPane,
				viewPort.getMainScaler(), this);
		this.filterExpression = trackSpec.getFilterExpression();
		subscribe();
	}
	
	protected ImgButton closeTrackButton() {
		ImgButton closeButton = new ImgButton();
		closeButton.setSrc(ResourceManager.IMAGE_CLOSE);
		closeButton.setWidth(16);
		closeButton.setHeight(16);
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SectionStack stack = Track.this.getSectionStack();
				stack.removeSection(findIndex(stack,Track.this));
			}
		});
		return closeButton;
	}

	protected int findIndex(SectionStack stack, SectionStackSection section) {
		int i = 0;
		for (SectionStackSection sect: stack.getSections()) {
			if (sect == section) break;
			i++;
		}
		return i;
	}
	// public Track(final ViewPort theViewPort, final String title, final String
	// description, final String featureSource, final RendererSettings
	// rendererSettings, final String filterExpression) {
	// super(title);
	// setResizeable(true);
	// addDrawPane();
	// this.viewPort = theViewPort;
	// this.featureSource = featureSource;
	// this.featureDS = null;
	// this.renderer =
	// FeatureRenderer.getRenderer(rendererSettings,drawPane,viewPort.getMainScaler(),this);
	// this.filterExpression = filterExpression;
	// subscribe();
	// }

	protected void subscribe() {
		final Track thisTrack = this;
		EventBus.subscribe(EventbusTopic.VIEWPORT_CHANGED,
				new TopicSubscriber<ViewPort>() {
					@Override
					public void onEvent(Subscription subscription,ViewPort viewPort) {
						fetchAndDraw();
						// drawFetchDraw();
					}

					@Override
					public int getId() {
						return 0;
					}
				});
		EventBus.subscribe(EventbusTopic.FEATURE_SELECTED,
				new TopicSubscriber<FeatureSelection>() {
					@Override
					public void onEvent(Subscription subscription, final FeatureSelection selection) {
						if (selection.getSourceTrack() != thisTrack)
							return;
						selection.getDrawFeature().destroy();
						AsyncCallback<Feature> callback = new AsyncCallback<Feature>() {
							@Override
							public void onSuccess(Feature fullFeature) {
								EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
								EventBus.publish(new Event(EventbusTopic.SELECTED_FEATURE_FETCHED),fullFeature);
								FeatureTools.removeById(features, selection.getDrawFeature().getFeature().getId());
								features.add(fullFeature);
								try {
									draw();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
								EventBus.publish(new Event(EventbusTopic.ERROR),"Could not fetch feature: " + caught.getMessage());
							}
						};
						// ArgumentMap arguments = new
						// ArgumentMap(viewPort.getMainGenomicRegion());
						// arguments.put("id",
						// selection.getDrawFeature().getFeature().getId());
						// arguments.setZoomLevel(ZoomLevel.DETAIL);
						// arguments.setFilterExpression(filterExpression);
						try {
							// CachedFeatureServerAsync.getFeatureById(featureSource,
							// arguments, callback);
							FeatureServerAsync featureServer = GWT.create(FeatureServer.class);
							EventBus.publish(new Event(EventbusTopic.START_WAIT), null);
							featureDS.getFeatureById(featureServer, selection.getDrawFeature().getFeature().getId(), callback);
						} catch (Exception e) {
							EventBus.publish(new Event(EventbusTopic.END_WAIT),	null);
							EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch feature: " + e.getMessage());
						}
					}

					@Override
					public int getId() {
						return 0;
					}
				});
		EventBus.subscribe(EventbusTopic.DRAG_MOVE,
				new TopicSubscriber<Integer>() {
					@Override
					public void onEvent(Subscription subscription, Integer offset) {
						dragMove(offset);
					}
					@Override
					public int getId() {
						return 0;
					}
				});
	}

	protected void dragMove(Integer offset) {
		if (!MOVING) {
			MOVING = true;
//			if (drawFeatures.size() < 5) {
//				for (DrawFeature drawFeature : drawFeatures) {
//					drawFeature.moveBy(offset, 0);
//				}
//			} else {
				drawPane.moveBy(offset, 0);
//			}
		}
		MOVING = false;
	}

	protected void drawFetchDraw() {
		try {
			draw();
		} catch (Exception e) {
			e.printStackTrace();
			SC.logWarn("Exception caught while drawing track " + getTitle());
		}
		fetchAndDraw();
	}

	protected void fetchAndDraw() {
		if (!viewPort.complete())
			return;
		//final Date start = new Date();
		AsyncCallback<List<Feature>> callback = new AsyncCallback<List<Feature>>() {
			@Override
			public void onSuccess(List<Feature> result) {
				features = result;
				try {
					//Date fetch = new Date();
					//Long fetchTime = fetch.getTime() - start.getTime();
					draw();
					//Date draw = new Date();
					//Long drawTime = draw.getTime() - fetch.getTime();
					//SC.warn("Track: " + getTitle() + "\nFetch: " + fetchTime + "\nDraw: " + drawTime);
				} catch (Exception e) {
					e.printStackTrace();
					SC.logWarn("Exception caught while drawing track " + getTitle());
				}
				EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR),"Could not fetch features: " + caught.getMessage());
				EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
			}
		};
		// fetch wider area features: draw in invisible zone
		GenomicRegion region = viewPort.getFetchGenomicRegion();
		ArgumentMap arguments = new ArgumentMap(region);
		arguments.setMinFeatureWidth(viewPort.getMainScaler().getMinVisibleWidth());
		arguments.setZoomLevel(ZoomLevel.OUTLINE);
		arguments.setFilterExpression(filterExpression);
		try {
			// CachedFeatureServerAsync.getFeatures(featureSource, arguments,
			// callback);
			FeatureServerAsync featureServer = (FeatureServerAsync) ViewsGWT.getFeatureServer(); //GWT.create(FeatureServer.class);
			EventBus.publish(new Event(EventbusTopic.START_WAIT), null);
			// force calling the correct method of featureserver
			featureDS.getFeaturesByRegion(featureServer, region, filterExpression, viewPort.getMainScaler().getMinVisibleWidth(), viewPort.getMainScaler().getMinDetailWidth(), callback);
			// CachedFeatureServerAsync getFeaturesByRegion(featureDS, region,
			// filterExpression, viewPort.getMainScaler().getMinVisibleWidth(),
			// callback);
		} catch (Exception e) {
			EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
			EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch features: " + e.getMessage());
		}
	}

	protected void addDrawPane(int height) {
		drawPane = new DrawPane();
		// drawPane.setWidth("300%");
		// drawPane.setLeft("-100%");
		drawPane.setWidth100();
		drawPane.setHeight(height);
		drawPane.setCanDrag(true);
		// drawPane.addResizedHandler(new ResizedHandler() {
		//
		// @Override
		// public void onResized(ResizedEvent event) {
		// try {
		// draw();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		// });
		drawPane.addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				try {
					draw();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// drawPane.addResizedHandler(new ResizedHandler() {
		//
		// @Override
		// public void onResized(ResizedEvent event) {
		// try {
		// draw();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		// });
		drawPane.setDragRepositionAppearance(DragAppearance.TARGET);
		drawPane.addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				draggingStart = event.getX() - drawPane.getAbsoluteLeft();
				int roundedStart = viewPort.getMainScaler().nearestBaseBorder(draggingStart);
				switch (viewPort.getDragMode()) {
				case ZOOM:
				case ANNOTATE:
					if (selectionRect == null) {
						selectionRect = new DrawRect();
						selectionRect.setLineWidth(0);
						selectionRect.setFillColor("#FF0000");
						selectionRect.setFillOpacity(0.3F);
						selectionRect.setLeft(roundedStart);
						selectionRect.setWidth(1);
						selectionRect.setDrawPane(drawPane);
						selectionRect.draw();
					} else {
						selectionRect.resizeTo(0, drawPane.getHeight());
						selectionRect.moveTo(roundedStart, 0);
						selectionRect.show();
					}
					break;
				case MOVE:
					previousX = event.getX() - drawPane.getAbsoluteLeft();
					break;
				case DISABLED:
					break;
				}
			}
		});
		drawPane.addDragMoveHandler(new DragMoveHandler() {
			@Override
			public void onDragMove(DragMoveEvent event) {
				switch (viewPort.getDragMode()) {
				case ZOOM:
				case ANNOTATE:
					int origLeft = selectionRect.getLeft();
					selectionRect.resizeBy(viewPort.getMainScaler().nearestBaseBorder(event.getX() - drawPane.getAbsoluteLeft()) - selectionRect.getLeft() - selectionRect.getWidth(), 0);
					selectionRect.moveTo(origLeft, 0);
					break;
				case MOVE:
					if (Math.abs(event.getX() - previousX) > DRAG_SENSITIVITY) {
						EventBus.publish(new Event(EventbusTopic.DRAG_MOVE),event.getX() - drawPane.getAbsoluteLeft() - previousX);
						previousX = event.getX() - drawPane.getAbsoluteLeft();
					}
					break;
				case DISABLED:
					break;
				}
			}
		});
		drawPane.addDragStopHandler(new DragStopHandler() {
			@Override
			public void onDragStop(DragStopEvent event) {
				draggingEnd = event.getX() - drawPane.getAbsoluteLeft();
				// int roundedEnd =
				// viewPort.getMainScaler().nearestBaseBorder(draggingEnd);
				switch (viewPort.getDragMode()) {
				case ZOOM:
				case ANNOTATE:
					selectionRect.hide();
					EventBus.publish(new Event(EventbusTopic.DRAG_SELECT),new ScreenRegion(draggingStart, draggingEnd));
					break;
				case MOVE:
					ScreenRegion screenRegion = viewPort.getMainScreenRegion();
					int offset;
					if (viewPort.getStrand())
						offset = draggingEnd - draggingStart;
					else
						offset = draggingStart - draggingEnd;
					EventBus.publish(new Event(EventbusTopic.DRAG_SELECT), new ScreenRegion(screenRegion.getVisibleStart() - offset, screenRegion.getVisibleEnd() - offset));
					break;
				case DISABLED:
					break;
				}
			}
		});
		drawPane.setCursor(Cursor.ARROW);
		addItem(drawPane); // will ensure drawPane draws when track draws
	}

	protected void draw() throws Exception {
		drawPane.destroyItems();
		drawPane.moveTo(0, drawPane.getTop());
		drawFeatures = renderer.drawFeatures(features);
	}

	public TrackSpecification getTrackSpec() {
		return trackSpec;
	}

	public void setTrackSpec(TrackSpecification trackSpec) {
		this.trackSpec = trackSpec;
	}

	public DrawPane getDrawPane() {
		return drawPane;
	}

	public void setDrawPane(DrawPane drawPane) {
		this.drawPane = drawPane;
	}

	public ZoomLevel getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(ZoomLevel zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

}