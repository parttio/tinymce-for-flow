package org.vaadin.tinymce.imageuploads;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Service
public class FileSystemStorageService {

	private final Path rootLocation = Path.of("target/images");

	public FileSystemStorageService() {
		if(!rootLocation.toFile().exists()) {
			rootLocation.toFile().mkdir();
		}
	}

	public boolean store(MultipartFile file) {
		boolean scaledDown = false;
		try {
			if (file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new RuntimeException(
						"Cannot store file outside current directory.");
			}

			long size = file.getSize();

			try (InputStream inputStream = file.getInputStream()) {

				if(size > 500*1000) {
					// Rather big image, scale down
					BufferedImage image = ImageIO.read(inputStream);
					int targetMaxWidth = 1280;
					if(image.getWidth() < 1280) {
						// Something very badly compressed image data..
						targetMaxWidth = 800;
					}

					image = Scalr.resize(image, targetMaxWidth);
					if(file.getOriginalFilename().endsWith("png")) {
						ImageIO.write(image, "png", new FileOutputStream(destinationFile.toFile()));
					} else {
						ImageIO.write(image, "jpg", new FileOutputStream(destinationFile.toFile()));
					}
					scaledDown = true;
				} else {
					Files.copy(inputStream, destinationFile,
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to store file.", e);
		}
		return scaledDown;
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to read stored files", e);
		}

	}

	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new RuntimeException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Could not read file: " + filename, e);
		}
	}

}