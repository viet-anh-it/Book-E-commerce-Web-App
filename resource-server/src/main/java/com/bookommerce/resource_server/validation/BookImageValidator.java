package com.bookommerce.resource_server.validation;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.utils.ValidationUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

//@formatter:off
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookImageValidator implements ConstraintValidator<ValidBookImage, MultipartFile> {

    static Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/jpg");
    static Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");

    @Override
    public boolean isValid(MultipartFile bookImage, ConstraintValidatorContext context) {
        if (bookImage == null || bookImage.isEmpty()) {
            ValidationUtils.buildValidationMessage(null, "A blank file is not allowed", context);
            return false;
        }

        if (bookImage.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(bookImage.getContentType())) {
            ValidationUtils.buildValidationMessage(null, "Content-Type: " + bookImage.getContentType() + " is not allowed", context);
            return false;
        }

        String bookImageOrgiginalFileName = bookImage.getOriginalFilename();
        if (bookImageOrgiginalFileName == null || bookImageOrgiginalFileName.isBlank()) {
            ValidationUtils.buildValidationMessage(null, "File name [" + bookImageOrgiginalFileName + "] is not allowed", context);
            return false;
        }

        String bookImageFileExtension = bookImageOrgiginalFileName.toLowerCase()
            .substring(bookImageOrgiginalFileName.lastIndexOf("."));
        if (!ALLOWED_FILE_EXTENSIONS.contains(bookImageFileExtension)) {
            ValidationUtils.buildValidationMessage(null, "File extension [" + bookImageFileExtension + "] is not allowed", context);
            return false;
        }

        return true;
    }
}
