/**
 * This file is part of agora_elections.
 * Copyright (C) 2014-2016  Agora Voting SL <agora@agoravoting.com>

 * agora_elections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * agora_elections  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with agora_elections.  If not, see <http://www.gnu.org/licenses/>.
**/
package models

import play.api.db.slick._
import play.api.cache.Cache
import play.api.Play.current
import play.api._

import java.sql.Timestamp

/**
  * DAL - data access layer
  *
  * Provides a decoupled interface to persistence functions. Do caching here.
  *
  */
object DAL {

  /** straight mapping to models */
  object votes {

    def insert(vote: Vote)(implicit dbConfig : slick.backend.DatabaseConfig[slick.driver.JdbcProfile]) = {
      insertWithSession(vote)
    }
    def insertWithSession(vote: Vote)(implicit dbConfig : slick.backend.DatabaseConfig[slick.driver.JdbcProfile]) = {
      Votes.insert(vote)
    }

    def findByVoterId(voterId: String): List[Vote] = n {
      Votes.findByVoterId(voterId)
    }

    def findByElectionId(electionId: Long): List[Vote] = { 
      Votes.findByElectionId(electionId)
    }

    def findByElectionIdRange(electionId: Long, drop: Long, take: Long): List[Vote] = { 
      findByElectionIdRangeWithSession(electionId, drop, take)
    }

    def findByElectionIdRangeWithSession(electionId: Long, drop: Long, take: Long): List[Vote] = {
      Votes.findByElectionIdRange(electionId, drop, take)
    }

    def checkHash(id: Long, hash: String) = {
      Votes.checkHash(id, hash)
    }

    def count: Int = {
      Votes.count
    }

    def countForElection(electionId: Long): Int = {
      countForElectionWithSession(electionId)
    }

    def countForElectionWithSession(electionId: Long): Int = {
      Votes.countForElection(electionId)
    }

    def countUniqueForElection(electionId: Long): Int = {
      countUniqueForElectionWithSession(electionId)
    }

    def countUniqueForElectionWithSession(electionId: Long): Int = {
      Votes.countUniqueForElection(electionId)
    }

    def countForElectionAndVoter(electionId: Long, voterId: String): Int = {
      Votes.countForElectionAndVoter(electionId,voterId)
    }

    def byDay(electionId: Long): List[(String, Long)] = {
        byDayW(electionId)
    }

    def byDayW(electionId: Long): List[(String, Long)] = {
      Votes.byDay(electionId)
    }

    private def key(id: Long) = s"vote.$id"
  }

  /** adds a caching layer */
  object elections {

    def findById(id: Long): Option[Election] = {
      findByIdWithSession(id)
    }
    def findByIdWithSession(id: Long): Option[Election] = Cache.getAs[Election](key(id)) match {
      case Some(e) => {
        Some(e)
      }
      case None => {
        val election = Elections.findById(id)
        // set in cache if found
        election.map(Cache.set(key(id), _))
        election
      }
    }

    def count: Int = DB.withSession {
      Elections.count
    }

    def insert(election: Election) = {
      Cache.remove(key(election.id))
      Elections.insert(election)
    }

    def insertWithSession(election: Election) = {
      Cache.remove(key(election.id))
      Elections.insert(election)
    }

    def updateState(id: Long, state: String) = {
      Cache.remove(key(id))
      Elections.updateState(id, state)
    }

    def updateResults(id: Long, results: String) = {
      Cache.remove(key(id))
      Elections.updateResults(id, results)
    }

    def updateConfig(id: Long, config: String, start: Timestamp, end: Timestamp) = {
      Cache.remove(key(id))
      Elections.updateConfig(id, config, start, end)
    }

    def setPublicKeys(id: Long, pks: String) = {
      Cache.remove(key(id))
      Elections.setPublicKeys(id, pks)
    }

    def delete(id: Long) = {
      Cache.remove(key(id))
      Elections.delete(id)
    }

    private def key(id: Long) = s"election.$id"
  }
}
