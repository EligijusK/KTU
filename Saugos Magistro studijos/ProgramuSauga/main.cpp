// LD 2. Buferio kopijavimas netikrinant ilgio (Buffer Copy without Checking Size of Input).

#include <iostream>
#include <fstream>
#include <sys/stat.h>
#include <sys/types.h>

void ReadFromFile(char fileName[]);

int main() {
    char Last_name[5];
    char Last_name_Without_Overflow[21];
    char Answer[3];
    char FileData[207];
    try {
        // Block of code to tr
        printf("Enter your name first time: ");
        std::cin.getline(Last_name_Without_Overflow, 21);
        printf("Enter your name second time: ");
        std::cin >> Last_name;
        std::cout << "First read result: " << Last_name_Without_Overflow << " Second read result: " << Last_name
                  << std::endl;
        std::cout << "size comparison: " << sizeof(Last_name_Without_Overflow) << " vs " << sizeof(Last_name) << std::endl;
        std::cin >> Answer;
    }
    catch(...)
    {
        std::cout << "some kind of exception" << std::endl;
    }
    std::cout << "If u want to read file pleas write Yes if you want to quit program pleas write No" << std::endl;

    if(strcmp(Answer, "Yes") == 0)
    {
        ReadFromFile("../FileToRead.txt");
    }

    return 0;
}


void ReadFromFile(char fileName[])
{
    std::fstream Newfile;

    Newfile.open(fileName);
    if (!Newfile.is_open())
    {
        std::cout << "error opening file" << std::endl;
    }
    struct stat statbuf;
    stat( fileName, &statbuf );
    char achData[statbuf.st_size];
    Newfile.get(achData, statbuf.st_size);
    std::cout << achData << std::endl;
    char achDataCopied[170];
    strcpy(achDataCopied, achData);
    std::cout << achDataCopied << std::endl;
    std::cout << "size comparison: " << statbuf.st_size << " vs " << sizeof(achDataCopied);
}