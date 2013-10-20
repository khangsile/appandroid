appandroid
==========

Warning: When adding libraries you may get this error:

[2013-10-20 11:51:17 - bumpr] Found 5 versions of android-support-v4.jar in the dependency list,
[2013-10-20 11:51:17 - bumpr] but not all the versions are identical (check is based on SHA-1 only at this time).
[2013-10-20 11:51:17 - bumpr] All versions of the libraries must be the same at this time.
[2013-10-20 11:51:17 - bumpr] Versions found are:
[2013-10-20 11:51:17 - bumpr] Path: /Users/KhangSiLe/Documents/Workspace/Eclipse/bumpr.sdk/libs/android-support-v4.jar
[2013-10-20 11:51:17 - bumpr] 	Length: 556198
[2013-10-20 11:51:17 - bumpr] 	SHA-1: 4a6be13368bb64c5a0b0460632d228a1a915f58f
[2013-10-20 11:51:17 - bumpr] Path: /Users/KhangSiLe/Documents/Workspace/Eclipse/android-tools/library/libs/android-support-v4.jar
[2013-10-20 11:51:17 - bumpr] 	Length: 385685
[2013-10-20 11:51:17 - bumpr] 	SHA-1: 48c94ae70fa65718b382098237806a5909bb096e
[2013-10-20 11:51:17 - bumpr] Path: /Users/KhangSiLe/Documents/Workspace/Eclipse/SlidingMenu/library/libs/android-support-v4.jar
[2013-10-20 11:51:17 - bumpr] 	Length: 385685
[2013-10-20 11:51:17 - bumpr] 	SHA-1: 48c94ae70fa65718b382098237806a5909bb096e
[2013-10-20 11:51:17 - bumpr] Path: /Users/KhangSiLe/Documents/Workspace/Eclipse/JakeWharton-ActionBarSherlock-5a15d92/actionbarsherlock/libs/android-support-v4.jar
[2013-10-20 11:51:17 - bumpr] 	Length: 385685
[2013-10-20 11:51:17 - bumpr] 	SHA-1: 48c94ae70fa65718b382098237806a5909bb096e
[2013-10-20 11:51:17 - bumpr] Path: /Users/KhangSiLe/git/appandroid/libs/android-support-v4.jar
[2013-10-20 11:51:17 - bumpr] 	Length: 385685
[2013-10-20 11:51:17 - bumpr] 	SHA-1: 48c94ae70fa65718b382098237806a5909bb096e
[2013-10-20 11:51:17 - bumpr] Jar mismatch! Fix your dependencies

Solution: Delete all but one "android-support-v4.jar" jar and replace the deleted jars with the remaining jar. 
