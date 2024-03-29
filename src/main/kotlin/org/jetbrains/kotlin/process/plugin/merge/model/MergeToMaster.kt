package org.jetbrains.kotlin.process.plugin.merge.model

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.commands.Git
import git4idea.repo.GitRepositoryManager

lateinit var projectForMergeAction: Project

fun merge(branch: String) {
    val vcsRepoManager = VcsRepositoryManager.getInstance(projectForMergeAction)
    val brancher = GitBrancher.getInstance(projectForMergeAction)
    val repositories = GitRepositoryManager(projectForMergeAction, vcsRepoManager).repositories

    brancher.rebase(repositories, branch)
    brancher.checkout("master", false, repositories, null)
    brancher.merge(branch, GitBrancher.DeleteOnMergeOption.NOTHING, repositories)

    val git = Git.getInstance()
    repositories.forEach { repo ->
        repo.remotes.forEach { remote ->
            remote.pushUrls.forEach { url ->
                git.push(
                    repo,
                    remote.name,
                    url,
                    repo.currentBranch!!.fullName,
                    true)
            }
        }
    }
}