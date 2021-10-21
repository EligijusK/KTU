// Eikon.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "bsapi.h"
#include <stdlib.h>

/* Used to supress compiler warnings */
#ifndef UNREFERENCED_PARAMETER
    #define UNREFERENCED_PARAMETER(param)   (void)param
#endif

//Pirstu sablonu kiekis
#define TSET_SIZE 16

//Sesijos kintamasis su irenginiu
static ABS_CONNECTION conn=0;

//Sablonu nuorodu masyvas
static ABS_BIR* tset[TSET_SIZE];

//Operacijos "callback" funkcija
static void BSAPI
callback(const ABS_OPERATION* p_operation, ABS_DWORD msg, void* data)
{
    UNREFERENCED_PARAMETER(p_operation);
    
    switch(msg) {
        /* These messages just inform us how the interactive operation
         * progresses. Typical applications do not need it. */
        case ABS_MSG_PROCESS_BEGIN:
        case ABS_MSG_PROCESS_END:
            break;
            
        /* On some platforms, the biometric operastion can be suspended
         * when other process acquires sensor for other operation. */
        case ABS_MSG_PROCESS_SUSPEND:
            printf("   operation has been suspended\n");
            break;
        case ABS_MSG_PROCESS_RESUME:
            printf("   operation has been resumed\n");
            break;
            
        /* Sometimes some info how the operation progresses is sent. */
        case ABS_MSG_PROCESS_PROGRESS:
        {
            ABS_PROCESS_PROGRESS_DATA* progress_data = 
                                    (ABS_PROCESS_PROGRESS_DATA*) data;
            if(progress_data->Percentage <= 100) {
                printf("   operation in progress (%d%%)...\n", 
                                            (int)progress_data->Percentage);
            } else {
                printf("   operation in progress...\n");
            }
            break;
        }
        case ABS_MSG_PROCESS_SUCCESS:
            printf("   success\n");
            break;
        case ABS_MSG_PROCESS_FAILURE:
            printf("   failure\n");
            break;
        
        /* Prompt messages should inform the user that he should do 
         * something. */
        case ABS_MSG_PROMPT_SCAN:
            printf("   swipe the finger\n"); 
            break;
        case ABS_MSG_PROMPT_TOUCH:
            printf("   touch the sensor\n");
            break;
        case ABS_MSG_PROMPT_KEEP:
            printf("   keep finger on the sensor\n"); 
            break;
        case ABS_MSG_PROMPT_LIFT:
            printf("   lift your finger away from the sensor\n");
            break;
        case ABS_MSG_PROMPT_CLEAN:
            printf("   clean the sensor\n"); 
            break;
        
        /* Quality messages come if something went wrong. E.g. the user
         * did not scan his finger in the right way. */
        case ABS_MSG_QUALITY_CENTER_HARDER:
            printf("   bad quality: center and harder\n"); 
            break;
        case ABS_MSG_QUALITY_CENTER:
            printf("   bad quality: center\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_LEFT:
            printf("   bad quality: too left\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_RIGHT:
            printf("   bad quality: too right\n"); 
            break;
        case ABS_MSG_QUALITY_HARDER:
            printf("   bad quality: harder\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_LIGHT:
            printf("   bad quality: too light\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_DRY:
            printf("   bad quality: too dry\n");
            break;
        case ABS_MSG_QUALITY_TOO_SMALL:
            printf("   bad quality: too small\n");
            break;
        case ABS_MSG_QUALITY_TOO_SHORT:
            printf("   bad quality: too short\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_HIGH:
            printf("   bad quality: too high\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_LOW:
            printf("   bad quality: too low\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_FAST:
            printf("   bad quality: too fast\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_SKEWED:
            printf("   bad quality: too skewed\n"); 
            break;
        case ABS_MSG_QUALITY_TOO_DARK:
            printf("   bad quality: too dark\n"); 
            break;
        case ABS_MSG_QUALITY_BACKWARD:
            printf("   bad quality: backward movement detected\n"); 
            break;
        case ABS_MSG_QUALITY_JOINT:
            printf("   bad quality: joint detected\n"); 
            break;
        
        /* Navigation messages are sent only from ABSNavigate */
        case ABS_MSG_NAVIGATE_CHANGE:
			{
            ABS_NAVIGATION_DATA* msgdata = (ABS_NAVIGATION_DATA*) data;
			printf("   Navigation X = %d, Y = %d\n", (int)msgdata->DeltaX, (int)msgdata->DeltaY);
			break;
			}
        case ABS_MSG_NAVIGATE_CLICK:
			printf("   Navigation - CLICK\n");
            break;
            
        /* Real application would probably use some GUI to provide feedback
         * for user. On these messages the GUI dialog should be made vsiible
         * and invisible respectivelly. */
        case ABS_MSG_DLG_SHOW:
        case ABS_MSG_DLG_HIDE:
            break;
            
        /* Idle message can come only if flag ABS_OPERATION_FLAG_USE_IDLE
         * was specified in ABS_OPERATION::dwFlags (i.e. never in this sample).
         * If the flag is specified, this message comes very often, hence 
         * giving the callback a chance to cancel the operation with 
         * ABSCancelOperation() without long time delays. In multithreaded 
         * applications, canceling the operation from another thread can be
         * better alternative. Consult BSAPI documentation for more info about
         * the topic. */
        case ABS_MSG_IDLE:
            break;
    }
}

static ABS_OPERATION op = { 
    /* ID of the operation. We don't need to identify the operation in this 
     * sample. When non-zero, the ID identifies the operation and allows it
     * to be canceled from any other thread with ABSCancelOperation(). */
    0,         
    
    /* Arbitrary pointer, which allows application to pass any data into
     * the callback. Not used in this sample. */
    NULL,      
    
    /* Pointer to a simple callback implementation function. */
    callback,  
    
    /* Timeout. For example, lets set timeout to 60 sec. Note the value does 
     * not limit how long the operation (e.g. ABSVerify()) can take. The 
     * timeout only specifies time the operation waits for user to put his 
     * finger on a sensor. Zero would mean no timeout (i.e. the operation can 
     * never end if user never puts his finger on the sensor.) */
    60000,
    
    /* By default BSAPI places short time delays between sending some important
     * callback messages. The purpose of this is to guarantee that if multiple
     * messages come very closely in sequence, then the user still has enough
     * time to see all the messages and not just the lat one of the fast
     * sequence.
     *
     * For application developer, this simplifies callback implementation
     * which in most cases can be just showing an appropriate message in a 
     * window or dialog.
     *
     * However the time delays are not needed when user can see all history
     * of the messages, e.g. (as in this sample) the messages are outputted
     * to standard output stream. Hence we disable the time delays with with 
     * the flag ABS_OPERATION_FLAG_LL_CALLBACK here. */
    ABS_OPERATION_FLAG_LL_CALLBACK
};

static void pagalba(void)
{
    printf("Komandas sarasas:\n");
    printf("   x ... Iseiti\n");
}

int _tmain(int argc, _TCHAR* argv[])
{

    bool CheckFingerPrint = false;
	char buffer[256];
	ABS_STATUS res;
	int baigti=0;

	res=ABSInitialize();

	if(res == ABS_STATUS_OK){
		printf("Inicializavom BSAPI\n\n");
	}
	
	pagalba();

    ABS_STATUS res;
    ABS_DEVICE_LIST* dev_list;
    res = ABSEnumerateDevices("usb", &dev_list);
    /* Sukuriam nauja sesija su pasirinktu skaitytuvu */
    printf("Jungiames prie '%s'...\n", dev_list->List[0].DsnSubString);
    res = ABSOpen(dev_list->List[0].DsnSubString, &conn);

    for (int i = 0; i < 3; ++i) {
        res = ABSEnroll(conn, &op, &tset[i], 0);
        if(res == ABS_STATUS_OK) {
            printf("Naujas antspaudo sablonas itrauktas i saraso elementa Nr.%d\n", i);
        } else {
            printf("Ivyko klaida bandant itraukti pirsto antspauda Nr.%d\n", i);
        }
    }

//    patikrinama ar nuskaitytas pirsto antspaudas su pries tai nuskaitytu antsoaudu
//    ABS_LONG matching_slot;
//    res = ABSVerify(conn, &op, 1, &tset[0], &matching_slot, 0); if(matching_slot == 0) {
//        printf("Sutampa\n");
//    } else if(matching_slot < 0) {
//        printf("Nesutampa\n");
//    }


    int count = 0;
    ABS_BIR* tmp_tset[TSET_SIZE]; int tmp_slot[TSET_SIZE];/* Sukurti nauja laikina sablonu masyva */
    for(int i = 0; i < TSET_SIZE; i++) {
            if(tset[i] != NULL) {
                tmp_tset[count] = tset[i];
                tmp_slot[count] = i;
                count++;
            }
    }

    while(!baigti) {

    /* Tikrinama ar anspaudas yra sarase */
    ABS_LONG index;
    res = ABSVerify(conn, &op, count, tmp_tset, &index, 0);
    if (index >= 0) {
            printf("Antspaudas yra saraso elemente Nr.%d\n", tmp_slot[index]);
    } else {
            printf("Antspaudo sarase nera\n");
    }

    printf("\n");
    printf("\n>> ");
	scanf("%s", buffer);       

    switch(buffer[0]) {
            
            case '?':
            case 'h': pagalba(); break;
            case 'x': baigti = 1; break;
            
            default:  
                printf("Nera tokios komandos. '?' -  iskviesti komandu sarasa.\n"); 
                break;
        }
    }

    /* Atlaisvinti uzimtus bsapi bibliotekos resursus */
    ABSTerminate();

	return 0;
}

