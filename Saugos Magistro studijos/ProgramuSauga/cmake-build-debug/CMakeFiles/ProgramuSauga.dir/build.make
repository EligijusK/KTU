# CMAKE generated file: DO NOT EDIT!
# Generated by "MinGW Makefiles" Generator, CMake Version 3.19

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Disable VCS-based implicit rules.
% : %,v


# Disable VCS-based implicit rules.
% : RCS/%


# Disable VCS-based implicit rules.
% : RCS/%,v


# Disable VCS-based implicit rules.
% : SCCS/s.%


# Disable VCS-based implicit rules.
% : s.%


.SUFFIXES: .hpux_make_needs_suffix_list


# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

SHELL = cmd.exe

# The CMake executable.
CMAKE_COMMAND = "C:\Program Files\JetBrains\CLion 2021.1.3\bin\cmake\win\bin\cmake.exe"

# The command to remove a file.
RM = "C:\Program Files\JetBrains\CLion 2021.1.3\bin\cmake\win\bin\cmake.exe" -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = "F:\KTU\Saugos Magistro studijos\ProgramuSauga"

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = "F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug"

# Include any dependencies generated for this target.
include CMakeFiles/ProgramuSauga.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/ProgramuSauga.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/ProgramuSauga.dir/flags.make

CMakeFiles/ProgramuSauga.dir/main.cpp.obj: CMakeFiles/ProgramuSauga.dir/flags.make
CMakeFiles/ProgramuSauga.dir/main.cpp.obj: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug\CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/ProgramuSauga.dir/main.cpp.obj"
	C:\mingw-w64\x86_64-8.1.0-win32-seh-rt_v6-rev0\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles\ProgramuSauga.dir\main.cpp.obj -c "F:\KTU\Saugos Magistro studijos\ProgramuSauga\main.cpp"

CMakeFiles/ProgramuSauga.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/ProgramuSauga.dir/main.cpp.i"
	C:\mingw-w64\x86_64-8.1.0-win32-seh-rt_v6-rev0\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "F:\KTU\Saugos Magistro studijos\ProgramuSauga\main.cpp" > CMakeFiles\ProgramuSauga.dir\main.cpp.i

CMakeFiles/ProgramuSauga.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/ProgramuSauga.dir/main.cpp.s"
	C:\mingw-w64\x86_64-8.1.0-win32-seh-rt_v6-rev0\mingw64\bin\g++.exe $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "F:\KTU\Saugos Magistro studijos\ProgramuSauga\main.cpp" -o CMakeFiles\ProgramuSauga.dir\main.cpp.s

# Object files for target ProgramuSauga
ProgramuSauga_OBJECTS = \
"CMakeFiles/ProgramuSauga.dir/main.cpp.obj"

# External object files for target ProgramuSauga
ProgramuSauga_EXTERNAL_OBJECTS =

ProgramuSauga.exe: CMakeFiles/ProgramuSauga.dir/main.cpp.obj
ProgramuSauga.exe: CMakeFiles/ProgramuSauga.dir/build.make
ProgramuSauga.exe: CMakeFiles/ProgramuSauga.dir/linklibs.rsp
ProgramuSauga.exe: CMakeFiles/ProgramuSauga.dir/objects1.rsp
ProgramuSauga.exe: CMakeFiles/ProgramuSauga.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir="F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug\CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable ProgramuSauga.exe"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles\ProgramuSauga.dir\link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/ProgramuSauga.dir/build: ProgramuSauga.exe

.PHONY : CMakeFiles/ProgramuSauga.dir/build

CMakeFiles/ProgramuSauga.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles\ProgramuSauga.dir\cmake_clean.cmake
.PHONY : CMakeFiles/ProgramuSauga.dir/clean

CMakeFiles/ProgramuSauga.dir/depend:
	$(CMAKE_COMMAND) -E cmake_depends "MinGW Makefiles" "F:\KTU\Saugos Magistro studijos\ProgramuSauga" "F:\KTU\Saugos Magistro studijos\ProgramuSauga" "F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug" "F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug" "F:\KTU\Saugos Magistro studijos\ProgramuSauga\cmake-build-debug\CMakeFiles\ProgramuSauga.dir\DependInfo.cmake" --color=$(COLOR)
.PHONY : CMakeFiles/ProgramuSauga.dir/depend

