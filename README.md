Rookit
=======
Rookit is the ultimate :notes: Music Library Manager :notes:.

Features :headphones:
------
Rookit is inspired by many other library managers, not only musically themed. However, Rookit stands out as it aims to handle common managing issues by resorting to alternative solutions, providing some brand-new and attrative features:
 - Both audio and metadata are stored through [MongoDB](https://www.mongodb.com/), providing a new organization model that solves some of the common problems created by storing data in a filesystem model, while providing advanced queriyng features and automatic scaling, among many other features inherited by MongoDB.
 - A new music data model (separate [project](https://github.com/JPDSousa/rookit-data-model)), built from root and inspired by models such as [Spotify](https://javascriptgorilla.wordpress.com/2016/08/23/spotify-database-schema/), [Soundcloud](https://developers.soundcloud.com/docs/api/reference) and [MusicBrainz](https://musicbrainz.org/doc/MusicBrainz_Database/Schema).
 - A parsing algorithm (separate [project](https://github.com/JPDSousa/rookit-parser)), crafted not only to parse music files from tags and file name (using a customizable list of formats) but also to learn from previous parse results.
