ladybird
========

Eclipse import:

1. Install latest version of Eclipse (Kepler)
2. Import Ladybird : File -> Import -> Git -> Projects from Git -> URI 
3. Copy/paste in URL : https://github.com/yalelibrary/ladybird.git
4. In Authentication, specify your github acct (private repo)
5. You'll see the number of branches avaialable.
6. Choose local destination directory (your home folder where you keep code).
6b. Choose Initial branch 'dev'
7. For wizard dialog, choose 'Import as general project'
8. Finish
9. Window -> Show View -> Navigator
10. Right click on .project, and Team -> Ignore (adds to .gitignore)
11. File .gitignore appears
12. Right click on .gitignore, and again Team -> Ignore
13. Ready to make changes? Edit a file. Click on top level project -> Team -> Commit
14. Make sure any gitignored files are not checked. They shouldn't be in the box anyway if steps 10,11 were followed. Generally, we need to ignore .classpath, .settings folder, .project, .gitignore should never be comitted to github.
15. 
