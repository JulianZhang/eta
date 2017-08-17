{-
(c) The GRASP/AQUA Project, Glasgow University, 1992-1998

\section[Constants]{Info about this compilation}
-}

module ETA.Main.Constants where


hiVersion :: Integer
hiVersion = read cProjectVersionInt :: Integer

cProjectName, cProjectVersion, cProjectVersionNumbers, cProjectVersionInt, cProjectPatchLevel, cProjectPatchLevel1, cProjectPatchLevel2, cProjectHomeURL, cProjectIssueReportURL, ghcProjectVersion, ghcProjectVersionInt, ghcprojectPatchLevel, ghcProjectPatchLevel1, ghcProjectPatchLevel2
  :: String
cProjectName = "Compiler for the Eta Programming Language"
-- @VERSION_CHANGE@
cProjectVersion = "0.0.9b" ++ cProjectPatchLevel
cProjectVersionNumbers = "0.0.9." ++ cProjectPatchLevel
cProjectVersionInt = "9"
-- @BUILD_NUMBER@
cProjectPatchLevel = "2"
cProjectPatchLevel1 = "2"
cProjectPatchLevel2 = ""
cProjectHomeURL = "http://github.com/typelead/eta"
cProjectIssueReportURL = cProjectHomeURL ++ "/issues"
ghcProjectVersion = "7.10.3"
ghcProjectVersionInt = "710"
ghcprojectPatchLevel = "3"
ghcProjectPatchLevel1 = "3"
ghcProjectPatchLevel2 = ""

-- All pretty arbitrary:

mAX_TUPLE_SIZE :: Int
mAX_TUPLE_SIZE = 62 -- Should really match the number
                    -- of decls in Data.Tuple

mAX_CONTEXT_REDUCTION_DEPTH :: Int
mAX_CONTEXT_REDUCTION_DEPTH = 100
  -- Trac #5395 reports at least one library that needs depth 37 here

mAX_TYPE_FUNCTION_REDUCTION_DEPTH :: Int
mAX_TYPE_FUNCTION_REDUCTION_DEPTH = 200
  -- Needs to be much higher than mAX_CONTEXT_REDUCTION_DEPTH; see Trac #5395

wORD64_SIZE :: Int
wORD64_SIZE = 8

tARGET_MAX_CHAR :: Int
tARGET_MAX_CHAR = 0x10ffff

mAX_INTLIKE, mIN_INTLIKE, mAX_CHARLIKE, mIN_CHARLIKE, mAX_SPEC_AP_SIZE :: Int
mIN_INTLIKE = -16
mAX_INTLIKE = 16
mIN_CHARLIKE = 0
mAX_CHARLIKE = 255
mAX_SPEC_AP_SIZE = 7
