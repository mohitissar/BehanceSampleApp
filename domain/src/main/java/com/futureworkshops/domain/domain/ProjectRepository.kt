package com.futureworkshops.domain.domain

import com.futureworkshops.data.model.domain.Project
import com.futureworkshops.domain.data.database.dao.ProjectDAO
import com.futureworkshops.domain.data.database.entity.ProjectEntity
import com.futureworkshops.domain.data.network.BehanceApiService
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject


class ProjectRepository @Inject constructor(
        private val behanceApiService: BehanceApiService,
        private val projectDAO: ProjectDAO
) {

    fun loadProjects(map: Map<String, Any>): Single<List<Project>> {
        return behanceApiService.getProjects(map)
                .map { response ->
                    response.items.map { it.toModel() }
                }
                .doOnSuccess { items ->
                    projectDAO.insertAll(items.map { ProjectEntity.fromModel(it) })
                }
                .onErrorResumeNext { t: Throwable ->
                    if (t is UnknownHostException) {
                        projectDAO.getAll().toObservable()
                                .flatMapIterable { items -> items }
                                .map { item -> item.toModel() }
                                .toList()
                    } else {
                        Single.error(t)
                    }
                }
    }
}