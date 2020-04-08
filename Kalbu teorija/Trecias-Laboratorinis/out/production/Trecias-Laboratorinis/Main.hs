 module Main where
import System.IO
import Data.Data
import Data.List
import Text.Read

-- remove elements from list from 0 to n
removeEle :: Int -> [a] -> [a]
removeEle n xs
  | (n <= 0) || null xs = xs
  | otherwise = removeEle (n - 1) (tail xs)

mapMaybe :: (a -> Maybe b) -> [a] -> [b]
mapMaybe _ []     = []

mapMaybe f (x:xs) = 
  case f x of
    Just y  -> y : mapMaybe f xs
    Nothing -> mapMaybe f xs

-- get file handle with selected name
getFileHandle:: String -> IO Handle
getFileHandle name = openFile name ReadMode

-- append results to selected file recursively
writeLines fileName list listOriginalLength = do
    notEnd ((length list) > 0)
  where notEnd True = do
          file <- openFile fileName AppendMode
          hPutStrLn file ( show (head list))
          hClose file
          writeLines fileName (removeEle 1 list) listOriginalLength

-- read lines from file while file is not at end
getLines :: Handle -> IO [String]
getLines hndl = do
   eof <- hIsEOF hndl
   notEnded eof
  where notEnded False =  do
          let line = hGetLine hndl
          lineConverted <- line
          rest <- getLines hndl
          return (lineConverted:rest)
        notEnded True = return []


-- split lines as words
getArray :: [String] -> IO [[String]]
getArray list = do
    notEnded (length list > 0)
  where notEnded True =  do
          let lineConverted = words (head list)
          let firstVal = if (length lineConverted) > 0 then lineConverted !! 0 else ""
          let secondVal = if (length lineConverted) > 1 then lineConverted !! 1 else ""
          let word = [firstVal, secondVal]
          let removedEl = removeEle 1 list
          let restAnsw = getArray removedEl
          answ <- restAnsw
          return (word:answ)
        notEnded False = return []

-- reverse element, it works as reversing list
myReverse :: [a] -> [a]
myReverse [] = []
myReverse (x:xs) = (myReverse xs) ++ [x]


-- calculate reverse int sum
calcReverse :: [[Int]] -> IO [Int]
calcReverse list =
  notEnded ((length list) > 0) -- condition to work
  where
    notEnded True = do 
      let first = head list -- list of elements
      let forReverseFirst = show (first !! 0)
      let forReverseSecond = show (first !! 1)
      let firstReverse = read (myReverse forReverseFirst) :: Int -- reverse first element and convert to int
      let secondReverse = read (myReverse forReverseSecond) :: Int -- reverse second element and convert to int
      let sum = firstReverse + secondReverse -- sum of two reverse elements
      let resReverse = read (myReverse $ show sum) :: Int -- reverse answer
      let datList = removeEle 1 list -- remove one element from list every time until list doesnt have any elements
      let returnVal = calcReverse datList
      ans <- returnVal
      return (resReverse:ans) -- add las value to list
    notEnded False = do
      return []

main :: IO ()
main = do
  let myint = 1 ::Int
  --putStrLn "hello world"
  file <- getFileHandle "input.txt" 
  fileForFirst <- getFileHandle "input.txt" -- reader
  lenghtFileLines <- getLines fileForFirst -- read file for first line
  hClose fileForFirst
  let filterLenghtForFirst = filter (\x -> length x > 0) lenghtFileLines -- filter first list from emty lines
  let firstElement = words (filterLenghtForFirst !! 0) -- get first element
  let lenghtFile = if (length firstElement == 1) then (read (firstElement !! 0) :: Int)+1 -- adding one because we need to delete one value more
                    else -1 -- checks if first line is length or pair
  linesFromFile <- getLines file -- read lines from file
  let filterLenght = filter (\x -> length x > 0) linesFromFile -- filter all lines for empty lines
  let reverseList = reverse (filterLenght) -- create reverse list for removing elements by first element if it is given length
  let lengthRemove = (length reverseList) - lenghtFile -- calculate how many elements need to be removed
  if lenghtFile > 0 && length reverseList > 0 then do 
      let removed = removeEle lengthRemove reverseList -- remove elements from list by calculated elements
      let reverseBack = reverse removed -- reverse back list for using
      let stringArray = getArray reverseBack
      array <- stringArray
      let filterForUse = map (filter (\x -> length x > 0 )) array -- filtering for empy values
      let filterForUseSec = filter (\x -> length x > 1 ) filterForUse -- filtering for empy arrays
      let filterInt = map ( mapMaybe (\x -> readMaybe x :: Maybe Int) ) filterForUseSec -- removing elements that are not numbers
      let filterForUseThird = filter (\x -> length x > 1 ) filterInt -- filtering for empty arrays
      let answer = calcReverse filterForUseThird -- use calculation function
      answ <- answer
      file <- openFile "result.txt" WriteMode -- creates file if needed
      hPutStr file "" -- rewrite file with empty value
      hClose file -- close file
      writeLines "result.txt" answ (length answ) -- use function for writing answers to file  
    else if lenghtFile <= 0 && length filterLenght > 0 then do
      let stringArray = getArray filterLenght
      array <- stringArray
      let filterForUse = map (filter (\x -> length x > 0 )) array -- filtering for empy values
      let filterForUseSec = filter (\x -> length x > 1 ) filterForUse -- filtering for empy arrays
      let filterInt = map ( mapMaybe (\x -> readMaybe x :: Maybe Int) ) filterForUseSec -- removing elements that are not numbers
      let filterForUseThird = filter (\x -> length x > 1 ) filterInt -- filtering for empty arrays
      let answer = calcReverse filterForUseThird -- use calculation function
      answ <- answer
      file <- openFile "result.txt" WriteMode -- creates file if needed
      hPutStr file "" -- rewrite file with empty value
      hClose file -- close file
      writeLines "result.txt" answ (length answ) -- use function for writing answers to file 
    else print "Error"
    
--  something <- calculateAnswer linesFromFile
--  print $ show lenghtFile
-- check if string can be converted to int
