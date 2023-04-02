package domain.models

import java.time.LocalDateTime

case class Post (id: Option[Long],
                 author: Long,
                 title: String,
                 content: String,
                 date: LocalDateTime = LocalDateTime.now(),
                 description: Option[String] = None)


