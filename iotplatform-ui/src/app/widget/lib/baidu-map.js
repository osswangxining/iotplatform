/*
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import ngBaiduMap from 'angular-baidu-map';

var gmGlobals = {
    loadingGmId: null,
    gmApiKeys: {}
}

export default class TbBaiduMap {
    constructor($containerElement, initCallback, defaultZoomLevel, dontFitMapBounds, minZoomLevel, gmApiKey, gmDefaultMapType) {

        var tbMap = this;
        this.defaultZoomLevel = defaultZoomLevel;
        this.dontFitMapBounds = dontFitMapBounds;
        this.minZoomLevel = minZoomLevel;
        this.tooltips = [];
        this.defaultMapType = gmDefaultMapType;

        function clearGlobalId() {
            if (gmGlobals.loadingGmId && gmGlobals.loadingGmId === tbMap.mapId) {
                gmGlobals.loadingGmId = null;
            }
        }

        function displayError(message) {
            $containerElement.html( // eslint-disable-line angular/angularelement
                "<div class='error'>"+ message + "</div>"
            );
        }

        function initBaiduMap() {
            tbMap.map = new BMap.Map($containerElement[0]);
            this.map.centerAndZoom(new BMap.Point(116.404, 39.915), 15);
            if (initCallback) {
                initCallback();
            }

        }
        /* eslint-disable no-undef */

//        function getGoogleMapTypeId(mapType) {
//            var mapTypeId = google.maps.MapTypeId.ROADMAP;
//            if (mapType) {
//                if (mapType === 'hybrid') {
//                    mapTypeId = google.maps.MapTypeId.HYBRID;
//                } else if (mapType === 'satellite') {
//                    mapTypeId = google.maps.MapTypeId.SATELLITE;
//                } else if (mapType === 'terrain') {
//                    mapTypeId = google.maps.MapTypeId.TERRAIN;
//                }
//            }
//            return mapTypeId;
//        }

        /* eslint-enable no-undef */

        this.mapId = '' + Math.random().toString(36).substr(2, 9);
        this.apiKey = gmApiKey || '';

        window.gm_authFailure = function() { // eslint-disable-line no-undef, angular/window-service
            if (gmGlobals.loadingGmId && gmGlobals.loadingGmId === tbMap.mapId) {
                gmGlobals.loadingGmId = null;
                gmGlobals.gmApiKeys[tbMap.apiKey].error = 'Unable to authentificate for Google Map API.</br>Please check your API key.';
                displayError(gmGlobals.gmApiKeys[tbMap.apiKey].error);
            }
        };

        this.initMapFunctionName = 'initBaiduMap_' + this.mapId;

        window[this.initMapFunctionName] = function() { // eslint-disable-line no-undef, angular/window-service
            lazyLoad.load({ type: 'js', path: 'https://cdn.rawgit.com/googlemaps/v3-utility-library/master/markerwithlabel/src/markerwithlabel.js' }).then( // eslint-disable-line no-undef
                function success() {
                    gmGlobals.gmApiKeys[tbMap.apiKey].loaded = true;
                    initBaiduMap();
                    for (var p = 0; p < gmGlobals.gmApiKeys[tbMap.apiKey].pendingInits.length; p++) {
                        var pendingInit = gmGlobals.gmApiKeys[tbMap.apiKey].pendingInits[p];
                        pendingInit();
                    }
                    gmGlobals.gmApiKeys[tbMap.apiKey].pendingInits = [];
                },
                function fail(e) {
                    clearGlobalId();
                    gmGlobals.gmApiKeys[tbMap.apiKey].error = 'Google map api load failed!</br>'+e;
                    displayError(gmGlobals.gmApiKeys[tbMap.apiKey].error);
                }
            );

        };

        if (this.apiKey && this.apiKey.length > 0) {
            if (gmGlobals.gmApiKeys[this.apiKey]) {
                if (gmGlobals.gmApiKeys[this.apiKey].error) {
                    displayError(gmGlobals.gmApiKeys[this.apiKey].error);
                } else if (gmGlobals.gmApiKeys[this.apiKey].loaded) {
                    initBaiduMap();
                } else {
                    gmGlobals.gmApiKeys[this.apiKey].pendingInits.push(initBaiduMap);
                }
            } else {
                gmGlobals.gmApiKeys[this.apiKey] = {
                    loaded: false,
                    pendingInits: []
                };
                var googleMapScriptRes = 'http://api.map.baidu.com/api?v=2.0&ak=6dtvdbX5acH7wNZU4yPXGYL0';//https://maps.googleapis.com/maps/api/js?key='+this.apiKey+'&callback='+this.initMapFunctionName;

                gmGlobals.loadingGmId = this.mapId;
                lazyLoad.load({ type: 'js', path: googleMapScriptRes }).then( // eslint-disable-line no-undef
                    function success() {
                        setTimeout(clearGlobalId, 2000); // eslint-disable-line no-undef, angular/timeout-service
                    },
                    function fail(e) {
                        clearGlobalId();
                        gmGlobals.gmApiKeys[tbMap.apiKey].error = 'Baidu map api load failed!</br>'+e;
                        displayError(gmGlobals.gmApiKeys[tbMap.apiKey].error);
                    }
                );
            }
        } else {
            displayError('No Baidu Map Api Key provided!');
        }
    }

    inited() {
        return angular.isDefined(this.map);
    }

    /* eslint-disable no-undef */
    updateMarkerLabel(marker, settings) {
        var label = new BMap.Label("<div style='color:"+settings.labelColor+"'><b>"+settings.labelText+"</b></div>",{offset:new BMap.Size(20,-10)});
        marker.setLabel(label);
    }
    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
    updateMarkerColor(marker, color) {
        var pinColor = color.substr(1);
        var pinImage = new BMap.Icon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
            new BMap.Size(21, 34));
        marker.setIcon(pinImage);
    }
    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
//    updateMarkerImage(marker, settings, image, maxSize) {
//        var testImage = new Image();
//        testImage.onload = function() {
//            var width;
//            var height;
//            var aspect = testImage.width / testImage.height;
//            if (aspect > 1) {
//                width = maxSize;
//                height = maxSize / aspect;
//            } else {
//                width = maxSize * aspect;
//                height = maxSize;
//            }
//            var pinImage = {
//                url: image,
//                scaledSize : new BMap.Size(width, height)
//            }
//            marker.setIcon(pinImage);
//            if (settings.showLabel) {
//                marker.set('labelAnchor', new BMap.Point(100, height + 20));
//            }
//        }
//        testImage.src = image;
//    }
    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
    createMarker(location, settings, onClickListener, markerArgs) {
        //var height = 34;
        var pinColor = settings.color.substr(1);
        var pinImage = new BMap.Icon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
            new BMap.Size(21, 34));
        var pinShadow = new BMap.Icon("http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
            new BMap.Size(40, 37));
        var marker;
//        if (settings.showLabel) {
//            marker = new MarkerWithLabel({
//                position: location,
//                map: this.map,
//                icon: pinImage,
//                shadow: pinShadow,
//                labelContent: '<div style="color: '+ settings.labelColor +';"><b>'+settings.labelText+'</b></div>',
//                labelClass: "tb-labels",
//                labelAnchor: new google.maps.Point(100, height + 20)
//            });
//        } else {
            marker = new BMap.Marker(location, {
                icon: pinImage,
                shadow: pinShadow
            });
            this.map.addOverlay(marker);
//        }

        if (settings.useMarkerImage) {
           // this.updateMarkerImage(marker, settings, settings.markerImage, settings.markerImageSize || 34);
        }

        if (settings.displayTooltip) {
            this.createTooltip(marker, settings.tooltipPattern, settings.tooltipReplaceInfo, markerArgs);
        }

        if (onClickListener) {
            marker.addListener('click', onClickListener);
        }

        return marker;
    }

    removeMarker(marker) {
       this.map.removeOverlay(marker);//marker.setMap(null);
    }

    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
    createTooltip(marker, pattern, replaceInfo, markerArgs) {
        var popup = new BMap.InfoWindow({
            content: ''
        });
        marker.addListener('click', function() {
           this.map.openInfoWindow(popup, marker.getPosition());
        });
        log(markerArgs);
//        this.tooltips.push( {
//            markerArgs: markerArgs,
//            popup: popup,
//            pattern: pattern,
//            replaceInfo: replaceInfo
//        });
    }
    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
    updatePolylineColor(polyline, settings, color) {
//        var options = {
//            path: polyline.getPath(),
//            strokeColor: color,
//            strokeOpacity: settings.strokeOpacity,
//            strokeWeight: settings.strokeWeight,
//            map: this.map
//        };
        polyline.setStrokeColor(color);
        polyline.setStrokeOpacity(settings.strokeOpacity);
        polyline.setStrokeWeight(settings.strokeWeight);
    }
    /* eslint-enable no-undef */

    /* eslint-disable no-undef */
    createPolyline(locations, settings) {
        var polyline = new BMap.Polyline(locations, {
            strokeColor: settings.color,
            strokeOpacity: settings.strokeOpacity,
            strokeWeight: settings.strokeWeight
        });
        this.map.addOverlay(polyline);
        return polyline;
    }
    /* eslint-enable no-undef */

    removePolyline(polyline) {
        this.map.removeOverlay(polyline);
        //polyline.setMap(null);
    }

    /* eslint-disable no-undef */
    fitBounds(bounds) {
        if (this.dontFitMapBounds && this.defaultZoomLevel) {
            this.map.centerAndZoom(bounds.getCenter(),this.defaultZoomLevel);
            //this.map.setCenter(bounds.getCenter());
        } else {
//            var tbMap = this;
//            google.maps.event.addListenerOnce(this.map, 'bounds_changed', function() { // eslint-disable-line no-undef
//                if (!tbMap.defaultZoomLevel && tbMap.map.getZoom() > tbMap.minZoomLevel) {
//                    tbMap.map.setZoom(tbMap.minZoomLevel);
//                }
//            });
            this.map.setCenter(bounds.getCenter());
        }
    }
    /* eslint-enable no-undef */

    createLatLng(lat, lng) {
        return new BMap.Point(lat, lng); // eslint-disable-line no-undef
    }

    extendBoundsWithMarker(bounds, marker) {
        bounds.extend(marker.getPosition());
    }

    getMarkerPosition(marker) {
        return marker.getPosition();
    }

    setMarkerPosition(marker, latLng) {
        marker.setPosition(latLng);
    }

    getPolylineLatLngs(polyline) {
        return polyline.getPath().getArray();
    }

    setPolylineLatLngs(polyline, latLngs) {
        polyline.setPath(latLngs);
    }

    createBounds() {
        return new BMap.Bounds(); // eslint-disable-line no-undef
    }

    extendBounds(bounds, polyline) {
        if (polyline && polyline.getPath()) {
            var locations = polyline.getPath();
            for (var i = 0; i < locations.getLength(); i++) {
                bounds.extend(locations.getAt(i));
            }
        }
    }

    invalidateSize() {
       this.map.enableAutoResize();
    }

    getTooltips() {
        return this.tooltips;
    }

}