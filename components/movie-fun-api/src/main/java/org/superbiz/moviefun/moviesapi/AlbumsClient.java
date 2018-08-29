package org.superbiz.moviefun.moviesapi; /**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

public class AlbumsClient {


    private final String albumsUrl;
    private final RestOperations restOperations;
    private static ParameterizedTypeReference<List<AlbumInfo>> albumListType = new ParameterizedTypeReference<List<AlbumInfo>>() {
    };

    public AlbumsClient( String albumsUrl, RestOperations restOperations )
    {
        this.albumsUrl = albumsUrl;
        this.restOperations = restOperations;
    }

    public void addAlbum(AlbumInfo album) {
        restOperations.postForEntity(albumsUrl,album,AlbumInfo.class);
    }

    public AlbumInfo find(long id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl+"/"+id);
        builder.toUriString();
        ResponseEntity<AlbumInfo> entity = restOperations.getForEntity(builder.toUriString(), AlbumInfo.class);
        return entity.getBody();
    }

    public List<AlbumInfo> getAlbums() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl);
        return restOperations.exchange(builder.toUriString(), GET, null, albumListType).getBody();
    }

    public void deleteAlbum(AlbumInfo album) {
        restOperations.delete(albumsUrl+ "/" + album.getId());
    }

    public void updateAlbum(AlbumInfo album) {
        restOperations.put(albumsUrl+ "/" + album.getId(), album);
    }
}



