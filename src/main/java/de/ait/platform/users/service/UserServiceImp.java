package de.ait.platform.users.service;


import de.ait.platform.role.entity.Role;
import de.ait.platform.role.service.RoleService;
import de.ait.platform.users.dto.UserPhotoUrlDto;
import de.ait.platform.users.dto.UserRequestDto;
import de.ait.platform.users.dto.UserResponseDto;
import de.ait.platform.users.entity.User;
import de.ait.platform.users.exceptions.UserNotFound;
import de.ait.platform.users.reposittory.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserRepository repository;
    private final ModelMapper mapper;
    private final RoleService roleService;
    private final BCryptPasswordEncoder encoder;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");


    @Transactional
    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank() ||
                dto.getEmail() == null || dto.getEmail().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()
        ) {
            throw new IllegalArgumentException("Required fields must not be null or empty");
        }
        repository.findUserByUsername(dto.getUsername()).ifPresent(u -> {
            throw new RuntimeException("User " + u.getUsername() + " already exists");
        });

        Role role = roleService.getRoleByTitle("ROLE_USER");
        HashSet<Role> setRole = new HashSet<>();
        setRole.add(role);
        String encodedPassword = encoder.encode(dto.getPassword());
        User newUser = repository.save(new User(null, dto.getUsername(), dto.getFirstName(), dto.getLastName(), dto.getEmail(), encodedPassword, dto.getPhoto(), setRole));
        return mapper.map(newUser, UserResponseDto.class);


    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        // Retrieve user by ID
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + id));

        // Update user properties if present
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            existingUser.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            existingUser.setLastName(dto.getLastName());
        }
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            existingUser.setPhoto(dto.getPhoto());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String encodedPassword = encoder.encode(dto.getPassword());
            existingUser.setPassword(encodedPassword);
        }
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Role roleName : dto.getRoles()) {
                Role role = roleService.getRoleByTitle(roleName.getTitle());
                if (role != null) {
                    roles.add(role);
                } else {
                    throw new IllegalArgumentException("Role not found: " + roleName);
                }
            }
            existingUser.setRoles(roles);
        }

        // Save updated user and return response
        User updatedUser = repository.save(existingUser);
        return mapper.map(updatedUser, UserResponseDto.class);
    }

    @Transactional
    @Override
    public UserResponseDto deleteUser(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            repository.deleteById(id);
        } else {
            throw new UserNotFound("Error deleting user. Couldn't find user with id:" + id);
        }
        return mapper.map(user, UserResponseDto.class);
    }

    @Transactional
    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(u -> mapper.map(u, UserResponseDto.class)).toList();
    }

    @Transactional
    @Override
    public UserResponseDto getUserById(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            return mapper.map(user.get(), UserResponseDto.class);
        } else {
            String message = "User with id: " + id + " not found";
            throw new UserNotFound(message);
        }
    }

    @Transactional
    @Override
    public List<UserResponseDto> getUserByEmail(String email) {
        Predicate<User> predicateByEmail = (email.equals("")) ? u -> true : user -> user.getEmail().equalsIgnoreCase(email);
        List<User> userList = repository.findAll().stream().filter(predicateByEmail).toList();
        return userList.stream().map(user -> mapper.map(user, UserResponseDto.class)).toList();
    }

    @Transactional
    @Override
    public UserResponseDto setAdminRole(String email, boolean admin) {
        return null;
    }

    @Transactional
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return  repository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User with login " + username + " not found"));
    }

    @Transactional
    @Override
    public boolean isUsernameTaken(String username) {
        return repository.existsByUsername(username);
    }

    @Transactional
    @Override
    public boolean isEmailTaken(String email) {
        return repository.existsByEmail(email);

    }



    @Transactional
    @Override
    public String addPhotoByUrl(UserPhotoUrlDto photoUrl) {
        User currentUser = loadCurrentUser(); // Получаем текущего пользователя
        currentUser.setPhoto(String.valueOf(photoUrl)); // Обновляем URL фото
        repository.save(currentUser); // Сохраняем изменения
        return "Photo URL added: " + photoUrl;
    }
    private User loadCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return repository.findUserByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }
        throw new RuntimeException("No authenticated user found");
    }

    @Transactional
    @Override
    public String addPhotoByFile(MultipartFile photoFile) {
        User currentUser = loadCurrentUser(); // Получаем текущего пользователя

        // Логика сохранения файла на сервере
        String filePath = saveFile(photoFile); // Метод сохранения файла
        currentUser.setPhoto(filePath); // Обновляем URL фото
        repository.save(currentUser); // Сохраняем изменения

        return "Photo added: " + filePath;
    }

    private String saveFile(MultipartFile file) {
        // Проверка, что файл не пустой
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file");
        }

        // Получение оригинального имени файла
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        // Проверка расширения файла
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension);
        }

        // Генерация уникального имени файла для избежания конфликтов
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // Определение пути для сохранения файла

        String directoryPath = "https://drive.google.com/drive/folders/1Ucbre_N_NUSEBJ7jxVa2DysuRzXPTi8C"; // Убедитесь, что путь существует
        Path filePath = Paths.get(directoryPath + uniqueFileName);

        try {
            // Создание директории, если она не существует
            Files.createDirectories(filePath.getParent());

            // Сохранение файла
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }

        return filePath.toString(); // Возвращаем полный путь к файлу
    }

}