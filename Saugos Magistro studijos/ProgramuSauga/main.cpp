// LD 2. Buferio kopijavimas netikrinant ilgio (Buffer Copy without Checking Size of Input).

#include <iostream>
#include <fstream>
#include <sys/stat.h>
#include <string>
#include <iomanip>

void ReadFromFile(char fileName[]);

int main() {
    char Last_name[5];
    char Answer[3];
    std::string Last_name_Without_Overflow;
//    char FileData[207];

        // Block of code to tr
        std::cout << "Enter your name first time: " << std::endl;
        std::cin >> Last_name_Without_Overflow;
        std::cout << "Enter your name first time: " << std::endl;
        std::cin >> Last_name;
        std::cout << "First read result: " << Last_name_Without_Overflow << " Second read result: " << Last_name << std::endl;
        std::cout << "size comparison: " << Last_name_Without_Overflow.size() << " vs " << sizeof(Last_name) << std::endl;

    std::cout << "If u want to read file pleas write Yes if you want to quit program pleas write No" << std::endl;
    std::cin >> Answer;


    if(strcmp(Answer, "Yes") == 0)
    {
        ReadFromFile((char*)"../FileToRead.txt");
    }

    return 0;
}


void ReadFromFile(char* fileName)
{
    std::fstream newfile;

    newfile.open(fileName);
    if (!newfile.is_open())
    {
        std::cout << "error opening file" << std::endl;
    }
    struct stat statbuf;
    stat( fileName, &statbuf );
    char achData[statbuf.st_size-10];
    newfile.get(achData, statbuf.st_size);
    char achDataCopied[170];
    strcpy(achDataCopied, achData);
    char dataCopied[170];
    strncpy(dataCopied, achData, 170);
    std::cout << "size comparison: " << statbuf.st_size << " vs " << sizeof(achDataCopied) << " vs " <<  sizeof(dataCopied) << std::endl;
    std::cout << "text comparison: " << std::endl;
    std::cout << achDataCopied << std::endl;
    std::cout << dataCopied << std::endl;
}