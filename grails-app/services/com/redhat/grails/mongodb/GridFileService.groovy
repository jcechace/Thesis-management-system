package com.redhat.grails.mongodb
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.gridfs.GridFS
import com.mongodb.gridfs.GridFSDBFile
import com.mongodb.gridfs.GridFSFile
import com.mongodb.gridfs.GridFSInputFile
import com.redhat.grails.mongodb.exceptions.MongoSaveException
import org.bson.types.ObjectId
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse
/**
 * This class provides abstraction above basic GridGF CRUD operations.
 * It allows you to create, retrieve and delete files from persisted datastore.
 *
 * @author Jakub Cechacek
 * @version 0.1
 */
class GridFileService {
    public static final DEFAULT_DB = 'files'
    public static final DEFAULT_GROUP = 'files'

    def mongo
    def grailsApplication

    /**
     *
     *  A convenience method allowing usage of name parameters
     *  when saving file in mongo's GridFS
     *
     *
     * @param file File to be stored in Mongo's GridFS
     *
     * @param id Id which will be stored in file's metadata.
     *           This parameter should not be set together with field parameter.
     *
     * @param group Group identifier which will be stored in file's metadata.
     *              Default group "files" will be used if this parameter is not set
     *
     * @param object An object related to the file which is being saved.
     *
     * @param field Name of the field which will be used as value of id in file's metadata.
     *              Setting this parameter takes effect only in combination with object parameter
     *
     * @param bucket Name of the bucket in which the given file will be saved.
     *               If this parameter if not set and object parameter is provided the name
     *               will be derived from it's canonical class name, otherwise
     *               empty bucket name will be used.
     *
     * @param metadata Map representation of additional information.
     *
     * @param delete If true this method will remove file which matches given group,
     *               and id (not considering other metadata) before storing new file.
     *               If mongoId is provided then it will be used as the matching
     *               parameter instead. Keep in mind that combination of group and id
     *               doesn't have to be unique -- in that case all files matching
     *               the combination will be removed
     *
     * @param mongoId Unique identifier which will be used as a value of _id field.
     *                By default or when value of this field is null, new unique identifier
     *                will be generated.
     *
     *
     * @return Instance of GridFSFile representing stored file
     *         or null if wasn't saved properly.
     */
    def GridFSFile save(Map args) {
        if (!args?.file) {
            throw new MongoSaveException('File cannot be null')
        }
        // set default values
        args.get('field', 'id')

        args.get('bucket', getBucket(args?.object))
        args.get('group', DEFAULT_GROUP)
        args.get('id', args?.object?."${args.field}")
        args.get('metadata', [:])
        args.get('delete', false)
        args.get('mongoId', null)

        save(args.file, args.id, args.bucket, args.group, args.metadata, args.delete, args.mongoId)
    }

    /**
     *
     * This method provides functionality to store uploaded file in mongo's GridFS
     * datastore and seamlessly bind it to an instance of domain class
     *
     *
     * @param file File to be stored in Mongo's GridFS
     *
     * @param group Group identifier which will be stored in file's metadata.
     *              Default group "files" will be used if this parameter is not set
     *
     * @param object An object related to the file which is being saved.
     *
     * @param field Name of the field which will be used as value of id in file's metadata.
     *
     * @param metadata Map representation of additional information.
     *
     * @param delete If true this method will remove file which matches given group,
     *               and id (not considering other metadata) before storing new file.
     *               If mongoId is provided then it will be used as the matching
     *               parameter instead. Keep in mind that combination of group and id
     *               doesn't have to be unique -- in that case all files matching
     *               the combination will be removed
     *
     * @param mongoId Unique identifier which will be used as a value of _id field.
     *                By default or when value of this field is null, new unique identifier
     *                will be generated.
     *
     * @return Instance of GridFSFile representing stored file
     *         or null if wasn't saved properly.
     */
    def GridFSFile save(MultipartFile file, Object object, String field = 'id', String group = null,
                        Map metadata = [:], Boolean delete = false, mongoId = null) {

        save(file, getBucket(object), group, object."$field", metadata, delete, mongoId)
    }

    /**
     *
     *  This method provides functionality to store uploaded file in mongo's GridFS
     *
     *
     * @param file File to be stored in Mongo's GridFS
     *
     * @param id Id which will be stored in file's metadata.
     *           This parameter should not be set together with field parameter.
     *
     * @param group Group identifier which will be stored in file's metadata.
     *              Default group "files" will be used if this parameter is not set
     *
     * @param bucket Name of the bucket in which the given file will be saved.
     *               If this parameter if not set then empty bucket name will be used.
     *
     * @param metadata Map representation of additional information.
     *
     * @param delete If true this method will remove files which match given group,
     *               and id (not considering other metadata) before storing new file.
     *               If mongoId is provided then it will be used as the matching
     *               parameter instead. Keep in mind that combination of group and id
     *               doesn't have to be unique -- in that case all files matching
     *               the combination will be removed
     *
     * @param mongoId Unique identifier which will be used as a value of _id field.
     *                By default or when value of this field is null, new unique identifier
     *                will be generated.
     *
     *
     * @return Instance of GridFSFile representing stored file
     *         or null if wasn't saved properly.
     */
    def GridFSFile save(MultipartFile file, Long id, String bucket = null, String group = null,
                        Map metadata = [:], Boolean delete = false, String mongoId = null) {

        group = group ?: DEFAULT_GROUP

        if (delete) {
            if (mongoId) {
                deleteFileByMongoId(mongoId, bucket)
            } else {
                deleteFile(id, group, bucket)
            }
        }

        def grid = getGrid(bucket)

        GridFSInputFile gfsFile = grid.createFile(file.getInputStream(), true)
        // set filename to match the one of original file
        gfsFile.filename = file.originalFilename
        if (mongoId) {
            gfsFile.id = new ObjectId(mongoId)
        }

        // build File metadata
        this.setMetadata(gfsFile, metadata, group, id)
        // set content type
        gfsFile.contentType = file.contentType
        // save the file
        _save(gfsFile)


    }


    private GridFSFile _save(GridFSInputFile gfsFile) {
        try {
            gfsFile.save()
            return gfsFile
        } catch (Exception e) {
            log.error("Error while saving file $gfsFile", e)
            return null
        }
    }

    /**
     * Removes all files matching given group bound the to given object from  GridFS
     *
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    def deleteFile(Object object, String field = 'id', String group = 'files', Map metadata = [:]) {
        metadata = [id: object."$field",
                group: group]

        deleteFile(metadata, getBucket(object))
    }

    /**
     * Removes all files matching given group and id from  GridFS
     *
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    def deleteFile(String id, String group = 'files', String bucket = null, Map metadata = [:]) {
        metadata << [id: id, group: group]

        deleteFile(metadata, bucket)
    }

    /**
     * Removes all files matching provided metadata from GridFS.
     *
     * @param query
     * @param bucket
     */
    def deleteFile(Map metadata, String bucket = null) {
        def grid = getGrid(bucket)
        def query = transformToQuery(metadata)
        grid.remove(query)
    }

    /**
     * Removes file from GridFS based on the value of its _id field
     *
     * @param mongoId
     * @param bucket
     */
    def deleteDomainFileByMongoId(String mongoId, Class clazz = null) {
        deleteFileByMongoId(mongoId, getBucket(clazz))
    }

    /**
     * Removes file from GridFS based on the value of its _id field
     *
     * @param mongoId
     * @param bucket
     */
    def deleteFileByMongoId(String mongoId, String bucket = null) {
        def grid = getGrid(bucket)
        def _id = new ObjectId(mongoId)

        grid.remove(grid.findOne(_id))
    }

    /**
     * Retrieves first file matching given group and id bound the to the given object from  GridFS
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    GridFSDBFile getFile(Object object, String field = 'id', String group = 'files', Map metadata = [:]) {
        metadata = [id: object."$field",
                group: group]

        getFile(metadata, getBucket(object))
    }

    /**
     * Retrieves first file matching given group and id from  GridFS
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    GridFSDBFile getFile(String id, String group = 'files', String bucket = null, Map metadata = [:]) {
        metadata << [id: id, group: group]

        getFile(metadata, bucket)
    }

    /**
     * Retrieves first file matching provided metadata from GridFS.
     *
     * @param query
     * @param bucket
     */
    GridFSDBFile getFile(Map metadata, String bucket) {
        def grid = getGrid(bucket)
        def query = transformToQuery(metadata)

        println("Searching GridFS for file. \nBucket: $bucket\nQuery: $query")

        grid.findOne(query)
    }

    /**
     * Retrieves file from GridFS based on the value of its _id field
     *
     * @param mongoId
     * @param bucket
     */
    GridFSDBFile getDomainFileByMongoId(String mongoId, Class clazz = null) {
        getFileByMongoId(mongoId, getBucket(clazz))
    }

    /**
     * Retrieves file from GridFS based on the value of its _id field
     *
     * @param mongoId
     * @param bucket
     */
    GridFSDBFile getFileByMongoId(String mongoId, String bucket = null) {
        def grid = getGrid(bucket)
        def _id = new ObjectId(mongoId)

        grid.findOne(_id)
    }

    /**
     * Retrieves first file matching given group and id bound the to the given object from  GridFS
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    List<GridFSDBFile> getAllFiles(Object object, String field = 'id', String group = 'files', Map metadata = [:]) {
        metadata = [id: object."$field",
                group: group]

        getAllFiles(metadata, getBucket(object))
    }

    /**
     * Retrieves all files matching given group and id from  GridFS
     *
     * @param id
     * @param group
     * @param bucket
     * @param metadata
     * @return
     */
    List<GridFSDBFile> getAllFiles(String id, String group = 'files', String bucket = null, Map metadata = [:]) {
        metadata << [id: id, group: group]

        getAllFiles(metadata, bucket)
    }

    /**
     * Retrieves all files matching provided metadata from GridFS.
     *
     * @param query
     * @param bucket
     */
    List<GridFSDBFile> getAllFiles(Map metadata, String bucket) {
        def grid = getGrid(bucket)
        def query = transformToQuery(metadata)

        def ret = grid.find(query)
    }


    def serveFile(HttpServletResponse response, GridFSDBFile file, Boolean save = false) {
        if (!file) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }

        response.contentType = file.contentType
        response.contentLength = file.length as int

        def attachment = save ? 'attachment' : 'inline'
        def filename = file.getFilename()
        response.setHeader("Content-disposition", "$attachment; filename=\"$filename\"")

        try {
            response.outputStream << file.inputStream
        } catch (Exception e) {
            log.error('Error when writing file into http response.', e)

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }

    private setMetadata(GridFSFile gridFSFile, Map metaMap, String group, Long fileId) {
        DBObject meta = gridFSFile.getMetaData() ?: new BasicDBObject(metaMap)

        // put group and fileId in metadata
        if (group) meta.put('group', group)
        if (fileId) meta.put('id', fileId)

        gridFSFile.setMetaData(meta)
    }

    private BasicDBObject transformToQuery(Map metadata) {
        metadata.collectEntries { key, value ->
            ["metadata.$key".toString(), value]
        } as BasicDBObject
    }

    private GridFS getGrid(String bucket = null) {
        def db = mongo.getDB(getGridDB())
        if (bucket) {
            return new GridFS(db, bucket)
        }
        new GridFS(db)
    }

    String getBucket(Object object) {
        getBucket(object?.class)
    }

    String getBucket(Class clazz) {
        def bucketMapping = GrailsClassUtils.getStaticFieldValue(clazz, 'bucketMapping')

        bucketMapping ?: clazz?.canonicalName.replaceAll('\\.', '_').toLowerCase()
    }

    String getBucket(String clazzName) {
        def clazz = this.class.classLoader.loadClass(clazzName)
        getBucket(clazz)
    }

    // Todo: retrieve Grid db name from app configuration
    private String getGridDB() {
        grailsApplication?.config?.grails?.mongo?.databaseName ?: DEFAULT_DB
    }
}
