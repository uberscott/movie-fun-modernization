package org.superbiz.moviefun;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.moviesapi.AlbumsClient;
import org.superbiz.moviefun.moviesapi.CoverInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AlbumsClient albumsClient;

    public AlbumsController( AlbumsClient albumsClient ) {
        this.albumsClient = albumsClient;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsClient.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsClient.find(albumId));
        return "albumDetails";
    }


        @PostMapping("/{albumId}/cover")
    public void uploadCover(@PathVariable Long albumId, @RequestParam("file") MultipartFile uploadedFile) {
        logger.debug("Uploading cover for album with id {}", albumId);

        if (uploadedFile.getSize() > 0) {
            try {

                albumsClient.uploadCover( albumId, uploadedFile.getInputStream(), uploadedFile.getContentType() );

            } catch (IOException e) {
                logger.warn("Error while uploading album cover", e);
            }
        }
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        CoverInfo ci = albumsClient.getCover(albumId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ci.contentType));
        headers.setContentLength(ci.content.length);

        return new HttpEntity<>(ci.content, headers);
    }




}
