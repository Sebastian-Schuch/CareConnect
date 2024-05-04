# Git Feature Workflow Guide

This guide will help you understand how to create your own feature on a new branch and how to merge it back to the main branch with a pull request.

## Creating a New Feature Branch

1. **Pull the latest changes from the remote repository**:
    ```bash
    git pull origin master
    ```

2. **Create a new branch for your feature**. Replace `<branch-name>` with your desired branch name:
    ```bash
    git checkout -b <branch-name>
    ```
This is a short form of: 
    ```bash
    git branch <branch-name>;
    git checkout <branch-name>
    ```

5. **Make changes in your branch**. Add files, make commits, and so on.

## Creating a Pull Request

1. **Push your changes to the remote repository**. Replace `<branch-name>` with your branch name:
    ```bash
    git push -u origin <branch-name>
    ```
The u flag sets your branch to upstream. This ensures that your local branch will be automatically used if you push or pull and does not require any further arguments.

## Merge request
1. Go to [merge requests]("https://reset.inso.tuwien.ac.at/repo/2024ss-se-pr-group/24ss-se-pr-qse-10/-/merge_requests"). 

2. Create a new merge request and select `<branch-name>` as the source branch. 
