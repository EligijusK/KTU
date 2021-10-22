// LD 2. Buferio kopijavimas netikrinant ilgio (Buffer Copy without Checking Size of Input).

#include <iostream>
#include <fstream>
#include <string>

void ReadFromFile(const char* fileName, std::ostream& log_stream);
void ReadFromFileUsingStrings(const char* fileName, std::ostream& log_stream);

const int value = 196; //file char count

int main() {
    std::ostream& log_stream = std::cout;
    std::string Last_name;
    std::string Last_name_Without_Overflow;

    log_stream << "Enter your name first time: " << std::endl;
    std::cin >> Last_name_Without_Overflow;
    log_stream << "Enter your name first time: " << std::endl;
    std::cin >> Last_name;
    log_stream << "First read result: " << Last_name_Without_Overflow << " Second read result: " << Last_name << std::endl;
    log_stream << "size comparison: " << Last_name_Without_Overflow.size() << " vs " << sizeof(Last_name) << std::endl;

    ReadFromFile("FileToRead.txt", log_stream);
    log_stream << std::endl;
    ReadFromFileUsingStrings("FileToRead.txt", log_stream);

    return 0;
}


void ReadFromFile(const char* fileName, std::ostream& log_stream)
{
    std::fstream newfile;

    newfile.open(fileName);
    if (!newfile.is_open())
    {
        log_stream << "error opening file" << std::endl;
    }

    const int size = 171;
    char achData[value];
    newfile.get(achData, value-1);
    char dataCopied[size];
    strncpy_s(dataCopied, achData, size-1);
    log_stream << "text comparison: " << std::endl;

    log_stream << achData << std::endl;
    log_stream << dataCopied << std::endl;
}

void ReadFromFileUsingStrings(const char* fileName, std::ostream& log_stream)
{
    std::ifstream in(fileName);
    std::string contents((std::istreambuf_iterator<char>(in)),
                         std::istreambuf_iterator<char>());

    std::string achData;
    achData = contents;
    contents[0] = 'a';

    log_stream << "text comparison: " << std::endl;
    log_stream << achData << std::endl;
    log_stream << contents << std::endl;
}