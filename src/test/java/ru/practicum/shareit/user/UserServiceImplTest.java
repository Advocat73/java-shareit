package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;

    private UserDto userDto1;
    private User user1;
    private UserDto userDto2;
    private User user2;
    private UserDto userUpdDto;
    private User userUpd;

    private final EasyRandom generator = new EasyRandom();
    private final UserMapper userMapper = new UserMapperImpl();

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        userDto1 = createUserDto(USER1_ID);
        user1 = userMapper.fromUserDto(userDto1);
        userDto2 = createUserDto(USER2_ID);
        user2 = userMapper.fromUserDto(userDto2);
        userUpdDto = createUserDto(USER1_ID);
        userUpd = userMapper.fromUserDto(userUpdDto);
    }

    @Test
    void addNewUserEmailNotValid() {
        when(userRepository.save(user1)).thenThrow(new DataConflictException(""));
        DataConflictException e = assertThrows(DataConflictException.class,
                () -> userService.addNewUser(userDto1));
        assertEquals("Пользователь по имени " + user1.getName() + " не добавлен в БД из-за неверных данных", e.getMessage());
    }

    @Test
    void addNewUser() {
        when(userRepository.save(user1)).thenReturn(user1);
        UserDto userDtoReceived = userService.addNewUser(userDto1);
        assertEquals(USER1_ID, userDtoReceived.getId());
        assertEquals(userDto1.getName(), userDtoReceived.getName());
        assertEquals(userDto1.getEmail(), userDtoReceived.getEmail());
    }

    @Test
    void updateUserNoUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userUpdDto, USER1_ID));
        assertEquals("Нет пользователя с ID: " + USER1_ID, e.getMessage());
    }

    @Test
    void updateUser() {
        when(userRepository.findById(USER1_ID)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(user1)).thenReturn(user1);
        UserDto userDtoReceived = userService.updateUser(userUpdDto, USER1_ID);
        assertEquals(USER1_ID, userDtoReceived.getId());
        assertEquals(userUpdDto.getName(), userDtoReceived.getName());
        assertEquals(userUpdDto.getEmail(), userDtoReceived.getEmail());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals(USER1_ID, users.get(0).getId());
        assertEquals(USER2_ID, users.get(1).getId());
        assertEquals(user1.getName(), users.get(0).getName());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
    }

    @Test
    void getUserNoUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.getUser(USER1_ID));
        assertEquals("Нет пользователя с ID: " + USER1_ID, e.getMessage());
    }

    @Test
    void getUser() {
        when(userRepository.findById(USER1_ID)).thenReturn(Optional.ofNullable(user1));
        UserDto userDtoReceived = userService.getUser(USER1_ID);
        assertEquals(USER1_ID, userDtoReceived.getId());
        assertEquals(user1.getName(), userDtoReceived.getName());
        assertEquals(user1.getEmail(), userDtoReceived.getEmail());
    }

    @Test
    void deleteUserNoUser() {
        userService.deleteUser(USER1_ID);
    }

    private UserDto createUserDto(Long userId) {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(userId);
        String email = userDto.getEmail() + "@mail.ru";
        userDto.setEmail(email);
        return userDto;
    }
}