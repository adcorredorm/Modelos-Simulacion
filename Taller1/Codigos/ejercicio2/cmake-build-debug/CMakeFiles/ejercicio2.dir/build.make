# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.10

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /home/sebastian/.local/share/JetBrains/Toolbox/apps/CLion/ch-1/181.4668.70/bin/cmake/bin/cmake

# The command to remove a file.
RM = /home/sebastian/.local/share/JetBrains/Toolbox/apps/CLion/ch-1/181.4668.70/bin/cmake/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/sebastian/CLionProjects/ejercicio2

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/sebastian/CLionProjects/ejercicio2/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/ejercicio2.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/ejercicio2.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/ejercicio2.dir/flags.make

CMakeFiles/ejercicio2.dir/main.cpp.o: CMakeFiles/ejercicio2.dir/flags.make
CMakeFiles/ejercicio2.dir/main.cpp.o: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/sebastian/CLionProjects/ejercicio2/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/ejercicio2.dir/main.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/ejercicio2.dir/main.cpp.o -c /home/sebastian/CLionProjects/ejercicio2/main.cpp

CMakeFiles/ejercicio2.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/ejercicio2.dir/main.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/sebastian/CLionProjects/ejercicio2/main.cpp > CMakeFiles/ejercicio2.dir/main.cpp.i

CMakeFiles/ejercicio2.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/ejercicio2.dir/main.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/sebastian/CLionProjects/ejercicio2/main.cpp -o CMakeFiles/ejercicio2.dir/main.cpp.s

CMakeFiles/ejercicio2.dir/main.cpp.o.requires:

.PHONY : CMakeFiles/ejercicio2.dir/main.cpp.o.requires

CMakeFiles/ejercicio2.dir/main.cpp.o.provides: CMakeFiles/ejercicio2.dir/main.cpp.o.requires
	$(MAKE) -f CMakeFiles/ejercicio2.dir/build.make CMakeFiles/ejercicio2.dir/main.cpp.o.provides.build
.PHONY : CMakeFiles/ejercicio2.dir/main.cpp.o.provides

CMakeFiles/ejercicio2.dir/main.cpp.o.provides.build: CMakeFiles/ejercicio2.dir/main.cpp.o


CMakeFiles/ejercicio2.dir/lcgrand.c.o: CMakeFiles/ejercicio2.dir/flags.make
CMakeFiles/ejercicio2.dir/lcgrand.c.o: ../lcgrand.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/sebastian/CLionProjects/ejercicio2/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object CMakeFiles/ejercicio2.dir/lcgrand.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/ejercicio2.dir/lcgrand.c.o   -c /home/sebastian/CLionProjects/ejercicio2/lcgrand.c

CMakeFiles/ejercicio2.dir/lcgrand.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/ejercicio2.dir/lcgrand.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/sebastian/CLionProjects/ejercicio2/lcgrand.c > CMakeFiles/ejercicio2.dir/lcgrand.c.i

CMakeFiles/ejercicio2.dir/lcgrand.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/ejercicio2.dir/lcgrand.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/sebastian/CLionProjects/ejercicio2/lcgrand.c -o CMakeFiles/ejercicio2.dir/lcgrand.c.s

CMakeFiles/ejercicio2.dir/lcgrand.c.o.requires:

.PHONY : CMakeFiles/ejercicio2.dir/lcgrand.c.o.requires

CMakeFiles/ejercicio2.dir/lcgrand.c.o.provides: CMakeFiles/ejercicio2.dir/lcgrand.c.o.requires
	$(MAKE) -f CMakeFiles/ejercicio2.dir/build.make CMakeFiles/ejercicio2.dir/lcgrand.c.o.provides.build
.PHONY : CMakeFiles/ejercicio2.dir/lcgrand.c.o.provides

CMakeFiles/ejercicio2.dir/lcgrand.c.o.provides.build: CMakeFiles/ejercicio2.dir/lcgrand.c.o


# Object files for target ejercicio2
ejercicio2_OBJECTS = \
"CMakeFiles/ejercicio2.dir/main.cpp.o" \
"CMakeFiles/ejercicio2.dir/lcgrand.c.o"

# External object files for target ejercicio2
ejercicio2_EXTERNAL_OBJECTS =

ejercicio2: CMakeFiles/ejercicio2.dir/main.cpp.o
ejercicio2: CMakeFiles/ejercicio2.dir/lcgrand.c.o
ejercicio2: CMakeFiles/ejercicio2.dir/build.make
ejercicio2: CMakeFiles/ejercicio2.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/sebastian/CLionProjects/ejercicio2/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable ejercicio2"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/ejercicio2.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/ejercicio2.dir/build: ejercicio2

.PHONY : CMakeFiles/ejercicio2.dir/build

CMakeFiles/ejercicio2.dir/requires: CMakeFiles/ejercicio2.dir/main.cpp.o.requires
CMakeFiles/ejercicio2.dir/requires: CMakeFiles/ejercicio2.dir/lcgrand.c.o.requires

.PHONY : CMakeFiles/ejercicio2.dir/requires

CMakeFiles/ejercicio2.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/ejercicio2.dir/cmake_clean.cmake
.PHONY : CMakeFiles/ejercicio2.dir/clean

CMakeFiles/ejercicio2.dir/depend:
	cd /home/sebastian/CLionProjects/ejercicio2/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/sebastian/CLionProjects/ejercicio2 /home/sebastian/CLionProjects/ejercicio2 /home/sebastian/CLionProjects/ejercicio2/cmake-build-debug /home/sebastian/CLionProjects/ejercicio2/cmake-build-debug /home/sebastian/CLionProjects/ejercicio2/cmake-build-debug/CMakeFiles/ejercicio2.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/ejercicio2.dir/depend

