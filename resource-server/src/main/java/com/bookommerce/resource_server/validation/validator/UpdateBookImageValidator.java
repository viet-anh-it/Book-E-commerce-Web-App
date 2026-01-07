package com.bookommerce.resource_server.validation.validator;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.dto.request.UpdateBookByIdRequestDto;
import com.bookommerce.resource_server.utils.ValidationUtils;
import com.bookommerce.resource_server.validation.annotation.ValidUpdateBookImage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

//@formatter:off
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateBookImageValidator implements ConstraintValidator<ValidUpdateBookImage, UpdateBookByIdRequestDto> {

    static Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/jpg");
    static Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");
    static String IMAGE_URL_PATH_PATTERN = "^/images/books/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\.(jpg|jpeg|png)$";

    @Override
    public boolean isValid(UpdateBookByIdRequestDto updateBookByIdRequestDto, ConstraintValidatorContext context) {
        String imageUrlPath = updateBookByIdRequestDto.imageUrlPath();
        MultipartFile imageFile = updateBookByIdRequestDto.image();

        boolean imageUrlPathMissing = imageUrlPath == null || imageUrlPath.isBlank();
        boolean imageFileMissing = imageFile == null || imageFile.isEmpty();

        if (imageUrlPathMissing && imageFileMissing) {
            ValidationUtils.buildValidationMessage("image", "A blank file is not allowed", context);
            return false;
        } else if (imageUrlPathMissing) {
            return validateImageFile(imageFile, context);
        } else if (imageFileMissing) {
            return validateImageUrlPath(imageUrlPath, context);
        } else {
            return validateImageFile(imageFile, context);
        }
    }

    private static boolean validateImageFile(MultipartFile imageFile, ConstraintValidatorContext context) {
        if (imageFile.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(imageFile.getContentType())) {
            ValidationUtils.buildValidationMessage(null, "Content-Type: " + imageFile.getContentType() + " is not allowed", context);
            return false;
        }

        String bookImageOrgiginalFileName = imageFile.getOriginalFilename();
        if (bookImageOrgiginalFileName == null || bookImageOrgiginalFileName.isBlank()) {
            ValidationUtils.buildValidationMessage(null, "File name [" + bookImageOrgiginalFileName + "] is not allowed", context);
            return false;
        }


        int lastDotIndex = bookImageOrgiginalFileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            ValidationUtils.buildValidationMessage(null, "File extension is missing", context);
            return false;
        }

        String bookImageFileExtension = bookImageOrgiginalFileName.toLowerCase().substring(lastDotIndex);
        if (!ALLOWED_FILE_EXTENSIONS.contains(bookImageFileExtension)) {
            ValidationUtils.buildValidationMessage(null,"File extension [" + bookImageFileExtension + "] is not allowed", context);
            return false;
        }  
        return true;
    }

    private static boolean validateImageUrlPath(String imageUrlPath, ConstraintValidatorContext context) {
        if (!imageUrlPath.matches(IMAGE_URL_PATH_PATTERN)) {
            ValidationUtils.buildValidationMessage(null,"Image URL path: [" + imageUrlPath + "] is not allowed", context);
            return false;
        }
        return true;
    }
}
