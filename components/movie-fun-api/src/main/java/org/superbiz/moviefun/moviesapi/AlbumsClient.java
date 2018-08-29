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

import org.apache.tika.io.IOUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import org.superbiz.moviefun.blobstore.Blob;

import javax.persistence.criteria.CriteriaQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        restOperations.postForEntity(albumsUrl+"/create",album,AlbumInfo.class);
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



   public void uploadCover( Long albumId, InputStream contentStream, String contentType ) throws IOException {

       byte[] payload = IOUtils.toByteArray(contentStream);
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.MULTIPART_FORM_DATA);


       LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
       headerMap.add("Content-disposition", "form-data; name=file;" );
       headerMap.add("Content-type", contentType );

       HttpEntity<byte[]> doc = new HttpEntity<byte[]>(payload, headerMap);

       LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
       multipartReqMap.add("file", doc);

       UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl+"/"+albumId+"/cover");

       HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
       ResponseEntity response = restOperations.exchange(builder.build().toUri(), HttpMethod.POST, reqEntity, Void.class );
    }

    public CoverInfo getCover(long albumId) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl+"/"+albumId+"/cover");

        HttpEntity<byte[]> entity = restOperations.getForEntity(builder.build().toUri(), byte[].class);

        CoverInfo rtn = new CoverInfo();
        rtn.contentType = entity.getHeaders().getContentType().getType();
        rtn.content = entity.getBody();

        return rtn;
    }

}



