{-# LANGUAGE CPP #-}
{-# LANGUAGE NoRebindableSyntax #-}
{-# OPTIONS_GHC -fno-warn-missing-import-lists #-}
module Paths_Trecias_Laboratorinis (
    version,
    getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir,
    getDataFileName, getSysconfDir
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

#if defined(VERSION_base)

#if MIN_VERSION_base(4,0,0)
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#else
catchIO :: IO a -> (Exception.Exception -> IO a) -> IO a
#endif

#else
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#endif
catchIO = Exception.catch

version :: Version
version = Version [0,1,0,0] []
bindir, libdir, dynlibdir, datadir, libexecdir, sysconfdir :: FilePath

bindir     = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\bin"
libdir     = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\lib\\x86_64-windows-ghc-8.8.3\\Trecias-Laboratorinis-0.1.0.0-JhZcwFX2sRiKXsyelPsP1X-Trecias-Laboratorinis"
dynlibdir  = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\lib\\x86_64-windows-ghc-8.8.3"
datadir    = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\share\\x86_64-windows-ghc-8.8.3\\Trecias-Laboratorinis-0.1.0.0"
libexecdir = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\libexec\\x86_64-windows-ghc-8.8.3\\Trecias-Laboratorinis-0.1.0.0"
sysconfdir = "C:\\Users\\Eligijus\\Desktop\\KTU\\Kalbu teorija\\Trecias-Laboratorinis\\.stack-work\\install\\7894289f\\etc"

getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir, getSysconfDir :: IO FilePath
getBinDir = catchIO (getEnv "Trecias_Laboratorinis_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "Trecias_Laboratorinis_libdir") (\_ -> return libdir)
getDynLibDir = catchIO (getEnv "Trecias_Laboratorinis_dynlibdir") (\_ -> return dynlibdir)
getDataDir = catchIO (getEnv "Trecias_Laboratorinis_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "Trecias_Laboratorinis_libexecdir") (\_ -> return libexecdir)
getSysconfDir = catchIO (getEnv "Trecias_Laboratorinis_sysconfdir") (\_ -> return sysconfdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "\\" ++ name)
