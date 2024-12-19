# torrentsDownloadBot

### Things TODO:

1) Put secrets inside image or start container with var secrets
2) Make mandatory volume for files
3) download torrents with more than 1 file or dir
4) Add DB for users and torrents and logging ( + /start /stop commands)
5) Auto build and docker image build (ci/cd)
6) make different threads for clients. only one active download for client. where to store clients??
7) add stop command, think how to differ downloads
8) remain downloads after container restart (DB) 
9) settings for delete or not files from disk(for self hosted purposes)
10) settings for zip multi files or just send as is one by one
11) settings for max torrent size
12) work with torrent files, not only with magnet links

### Off-topic

Test other torrent clients https://github.com/mpetazzoni/ttorrent for example (or write your own)
