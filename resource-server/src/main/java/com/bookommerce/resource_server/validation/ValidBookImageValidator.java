package com.bookommerce.resource_server.validation;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

//@formatter:off
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidBookImageValidator implements ConstraintValidator<ValidBookImage, MultipartFile> {

    static Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/jpg");
    static Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");

    @Override
    public boolean isValid(MultipartFile bookImage, ConstraintValidatorContext context) {
        if (bookImage == null || bookImage.isEmpty()) {
            buildValidationMessage("A blank file is not allowed", context);
            return false;
        }

        if (bookImage.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(bookImage.getContentType())) {
            buildValidationMessage("Content-Type: " + bookImage.getContentType() + " is not allowed", context);
            return false;
        }

        String bookImageOrgiginalFileName = bookImage.getOriginalFilename();
        if (bookImageOrgiginalFileName == null || bookImageOrgiginalFileName.isBlank()) {
            buildValidationMessage("File name [" + bookImageOrgiginalFileName + "] is not allowed", context);
            return false;
        }

        String bookImageFileExtension = bookImageOrgiginalFileName.toLowerCase()
            .substring(bookImageOrgiginalFileName.lastIndexOf("."));
        if (!ALLOWED_FILE_EXTENSIONS.contains(bookImageFileExtension)) {
            buildValidationMessage("File extension [" + bookImageFileExtension + "] is not allowed", context);
            return false;
        }

        return true;
    }

    private static void buildValidationMessage(String message, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
