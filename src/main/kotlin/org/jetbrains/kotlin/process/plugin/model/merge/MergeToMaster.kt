package org.jetbrains.kotlin.process.plugin.model.merge

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.commands.Git
import git4idea.repo.GitRepositoryManager

var branchName: String = PropertiesComponent.getInstance().getValue("branchName")!!
lateinit var projectForMergeAction: Project

fun merge() {
    val vcsRepoManager = VcsRepositoryManager.getInstance(projectForMergeAction)
    val brancher = GitBrancher.getInstance(projectForMergeAction)
    val repositories = GitRepositoryManager(projectForMergeAction, vcsRepoManager).repositories

    brancher.merge(branchName, GitBrancher.DeleteOnMergeOption.NOTHING, repositories)
//            brancher.rebase(repositories, branchName)

    //TODO: Are you really merging??
    val git = Git.getInstance()
    repositories.forEach { repo ->
        repo.remotes.forEach { remote ->
            remote.pushUrls.forEach { url ->
                val result = git.push(repo, remote.name, url, repo.currentBranch!!.fullName, true)
                println(result)
            }
        }
    }
}