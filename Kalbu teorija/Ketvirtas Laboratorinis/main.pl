% 1998-08-20 uzduotis: 10, 11
% Eligijus Kiudys IFF-7/14

run_opt(10) :-
    write('Sveiki, prasome iveskite dvieju dimensiju masyva pvz: [[labas, ajai], [antras, 2]]. BUTINAI su tasku gale(.):'), nl,
    read(List),
    listLength(List, Length),
    listLengthWithSingle(List, LengthAll),
    write('Benras Elementu ilgis neiskaitant vidini sarasa kaip elemento: '),
    write(Length), nl,
    write('Benras Elementu ilgis iskaitant vidini sarasa kaip elementa: '),
    write(LengthAll), nl.


run_opt(11) :-
    write('Sveiki, prasome iveskite dvieju dimensiju masyva pvz: [[2, "a"], [3, 1]] BUTINAI su tasku gale(.):'), nl,
    read(List),
    increasingList(List, Res),
    write('Gautas rezultatu masyvas:'), nl,
    write(Res).

run_opt(_) :- write('Blogas pasirinkimas'), nl, halt.

main :-
    write('Iveskite uzduoties numeri(10-11) su tasku gale(.):'), nl,
    read(Line),
    run_opt(Line).

%  task Nr. 10 start

findlen([],X):- % return zero if list is empty
    X=0.

findlen([X|Tail],Count):- % calculate list length using recurrsion. X is first element, and Tails is all other elements
    findlen(Tail,Prev),
    Count is Prev + 1.

listLength([], Lengths) :- % emty list sets length to 0
    Lengths=0.

listLength([Head|Tail], Lengths) :- % calculate 2D list lenght without inner list as element using double recurrsion
    findlen(Head, Answ), % inner list lenght
    listLength(Tail, LengthsTemp), % reccursion: inner linght + previuos inner lenght
    Lengths is LengthsTemp + Answ.

listLengthWithSingle([], Lengths) :- % emty list sets length to 0
    Lengths=0.

listLengthWithSingle([Head|Tail], Lengths) :-  % calculate 2D list lenght witht inner list as element using double recurrsion
    findlen(Head, Answ), % inner list lenght
    TempAnsw is Answ + 1, % adding one for count inner list as elemnt
    listLengthWithSingle(Tail, LengthsTemp), % reccursion: linght + previuos lenght
    Lengths is LengthsTemp + TempAnsw.

%  task Nr. 10 end

%  task Nr. 11 start

firstSecElements([First, Second | Tail], Size, Element) :- % split list into lenght and element for reapiting
    Size = First,
    Element = Second.

genListByElement(_,0,[]) :- !. % end recursive function when count is 0

genListByElement(Element,Count,[Element|Elements]) :- % create list and add same Element to it until count is 0
         CountTemp is Count-1,
         genListByElement(Element, CountTemp, Elements).


pushFront(Item, List, [Item|List]). % push element in front of list

genListString(_, _, 0, [34]) :- !. % add " as last element for string creation

genListString([Head|Tail], TempElement, Count, [Head|Elements]) :- % add string element to list as asiic II example: [4], [34, 97] - > [4, 34, 97]
         genListString(Tail, TempElement, Count, Elements).


genListString(Element, [Head | Tail], Count, Elements) :- % repete adding string all over again when all string elemnts are added until Count is 0
    (
    Element == [] ->
        CountTemp is Count-1,
        genListString([Head | Tail], [Head | Tail], CountTemp, Elements)
    ).

generateAtom(TempAnswer, [], Answ) :-
    Answ = TempAnswer.

generateAtom(Atom, [Head | Tail], Answ) :-
    atom_concat(Atom, Head, TempAnsw),
    generateAtom(TempAnsw, Tail, Answ).


generateDoubleList([ ], Start, Answ) :- % assign answer value
    Answ = Start.

generateDoubleList([Head | Tail], Empty, Answ) :-
    firstSecElements(Head, First, Second),
    (
    number(Second) -> % check if element is number

    genListByElement(Second, First, List), % create listh with reapiting elements
    TempList = List,
    append(Empty, [TempList], Value), % add list to list
    generateDoubleList(Tail, Value, Answ) ; % add elements while array is empty

    atom(Second) -> % check if element is char

        % if atom do this
        genListByElement(Second, First, List), % generate char list
        TempList = List,
        generateAtom('', TempList, Res), % combine list
        append(Empty, [[Res]], Value), % results append to list
        generateDoubleList(Tail, Value, Answ); % add elements while array is empty

        % if string
        genListString(Second, Second, First, List), % generate asiic II list with " as end
        pushFront(34, List, ResultList), % push to front "
        atom_codes(X, ResultList), % convert asiic list to atom
        append(Empty, [[X]], Value), % append res to list
        generateDoubleList(Tail, Value, Answ) % add elements while array is empty

    ).

increasingList(List, Res) :- % function for returning results
    generateDoubleList(List, [], Res).

%  task Nr. 11 end