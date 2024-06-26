package com.online.learning.service.impl;

import com.online.learning.model.dto.FileDTO;
import com.online.learning.service.FileUploadService;
import com.online.learning.utils.ConstantUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Override
    public FileDTO uploadFile(MultipartFile multipartFile, String type) {
        try {
            UUID uuid = UUID.randomUUID();
            String[] fileFrags = multipartFile.getOriginalFilename().split("\\.");
            String extension = fileFrags[fileFrags.length-1];
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            String uploadDir = ConstantUtil.PATH_FILE_UPLOAD + type.toLowerCase() + "/";
            fileName = uuid.toString() + "." + extension;

            boolean success = false;
            if (type.equalsIgnoreCase("EXAM") || type.equalsIgnoreCase("RESULT")) {
                fileName = multipartFile.getOriginalFilename();
                success = saveQuestionFile(uploadDir, fileName, multipartFile);
            } else {
                success = saveFile(uploadDir, fileName, multipartFile);
            }

            FileDTO fileDTO = null;
            if (success) {
                fileDTO = new FileDTO();
                fileDTO.setId(uuid.toString());
                fileDTO.setFileName(fileName);
                fileDTO.setName(multipartFile.getOriginalFilename());
                fileDTO.setPath(ConstantUtil.PATH_URL_UPLOAD + type.toLowerCase() + "/" + fileName);
                fileDTO.setSize(multipartFile.getSize());
            }

            return fileDTO;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public FileDTO uploadQuizExamFile(MultipartFile multipartFile) {
        try {
            UUID uuid = UUID.randomUUID();
            String[] fileFrags = Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.");
            String extension = fileFrags[fileFrags.length-1];
            String fileName;

            String uploadDir = ConstantUtil.PATH_FILE_UPLOAD + "exam/";
            fileName = uuid + "." + extension;

            boolean success = saveFile(uploadDir, fileName, multipartFile);

            FileDTO fileDTO = null;
            if (success) {
                fileDTO = new FileDTO();
                fileDTO.setId(uuid.toString());
                fileDTO.setFileName(fileName);
                fileDTO.setName(multipartFile.getOriginalFilename());
                fileDTO.setPath(ConstantUtil.PATH_FILE_UPLOAD + "exam/" + fileName);
                fileDTO.setSize(multipartFile.getSize());
            }

            return fileDTO;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<FileDTO> uploadMutilFile(MultipartFile[] multipartFiles, String type) {
        if (multipartFiles == null) {
            return null;
        }

        List<FileDTO> fileDTOList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null
                    && !multipartFile.getOriginalFilename().equalsIgnoreCase("")) {
                FileDTO fileDTO = uploadFile(multipartFile, type);
                if (fileDTO != null) {
                    fileDTOList.add(fileDTO);
                }
            }
        }
        return fileDTOList;
    }

    @Override
    public FileDTO downloadFile(String filePath) {
        return null;
    }

    private boolean saveFile(String uploadDir, String fileName, MultipartFile multipartFile) {
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException ioe) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }
    private boolean saveQuestionFile(String uploadDir, String fileName, MultipartFile multipartFile) {
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException ioe) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

}
