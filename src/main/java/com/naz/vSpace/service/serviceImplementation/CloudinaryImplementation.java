package com.naz.vSpace.service.serviceImplementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.service.CloudinaryService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CloudinaryImplementation implements CloudinaryService{
    @Resource
    private Cloudinary cloudinary;
    @Override
    public String uploadPhoto(MultipartFile photo) {
        if(photo.isEmpty()){
            throw new VSpaceException("No file selected");
        }
        if(!isImageFile(photo)){
            throw new VSpaceException("You can only upload an image: jpeg, png or gif");
        }
        try{
            Map uploadedFile = cloudinary.uploader().upload(photo.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "public_id", photo.getOriginalFilename(),
                            "use_filename", "true",
                            "unique_filename", false,
                            "overwrite", true
                    ));
            String secureUrl = uploadedFile.get("secure_url").toString();
            return cloudinary.url().secure(true).generate(secureUrl);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> uploadPhotos(MultipartFile[] photos) {
        if(photos.length == 0){
            throw new VSpaceException("No file selected");
        }
        List<String> urls = new ArrayList<>();
        for(MultipartFile photo: photos){
            if(!isImageFile(photo)){
                throw new VSpaceException("You can only upload an image: jpeg, png or gif");
            }
            try{
                Map uploadedFile = cloudinary.uploader().upload(photo.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "image",
                                "public_id", photo.getOriginalFilename(),
                                "use_filename", "true",
                                "unique_filename", false,
                                "overwrite", true
                        ));
                String secureUrl = uploadedFile.get("secure_url").toString();
                String url = cloudinary.url().secure(true).generate(secureUrl);
                urls.add(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return urls;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try{
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "public_id", file.getOriginalFilename(),
                            "use_filename", "true",
                            "unique_filename", false,
                            "overwrite", true
                    ));
            String secureUrl = uploadedFile.get("secure_url").toString();
            return cloudinary.url().secure(true).generate(secureUrl);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return MediaType.IMAGE_JPEG_VALUE.equals(contentType)||
                MediaType.IMAGE_GIF_VALUE.equals(contentType)||
                MediaType.IMAGE_PNG_VALUE.equals(contentType);
    }
}
